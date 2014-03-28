package cz.snappyapps.snappyrpc.client;

import cz.snappyapps.snappyrpc.Id;
import cz.snappyapps.snappyrpc.Name;
import cz.snappyapps.snappyrpc.Request;
import cz.snappyapps.snappyrpc.Response;
import cz.snappyapps.snappyrpc.client.marshaller.Marshaller;
import cz.snappyapps.snappyrpc.client.transporter.Transporter;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.annotation.Annotation;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.Callable;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;
import java.util.concurrent.atomic.AtomicInteger;

abstract class RpcInvocationHandler implements InvocationHandler {

    private static final Logger logger = LoggerFactory.getLogger(RpcInvocationHandler.class);

    private UUID uuid = UUID.randomUUID();
    private AtomicInteger idGenerator;
    private Transporter transporter;
    private Marshaller marshaller;
    private ConcurrentHashMap<Method, MetaDataCache> mdCache = new ConcurrentHashMap<java.lang.reflect.Method, MetaDataCache>();

    public int getId() {
        return idGenerator.get();
    }

    public void setIdGenerator(AtomicInteger idGenerator) {
        this.idGenerator = idGenerator;
    }

    public void setTransporter(Transporter transporter) {
        this.transporter = transporter;
    }

    public void setMarshaller(Marshaller marshaller) {
        this.marshaller = marshaller;
    }

    protected abstract ExecutorService getExecutorService();

    protected abstract Class<? extends Future> getFutureClass();

    /**
     * Unfortunately some serializers only come out with numbers as Doubles if they have no more specific
     * type available. So I decided to use properties of Number parent class and use the conversion
     * based on the return type from the signature.
     *
     * @param number       number instance
     * @param expectedType type specified in client interface
     * @return boxed specialized type according to expectedType
     */
    private static Object enforceCorrectNumberType(Number number, Class<?> expectedType) {
        if (number.getClass() == expectedType) {
            return expectedType.cast(number);
        } else if (expectedType == int.class || expectedType == Integer.class) {
            return number.intValue();
        } else if (expectedType == double.class || expectedType == Double.class) {
            return number.doubleValue();
        } else if (expectedType == long.class || expectedType == Long.class) {
            return number.longValue();
        } else if (expectedType == byte.class || expectedType == Byte.class) {
            return number.byteValue();
        } else if (expectedType == float.class || expectedType == Float.class) {
            return number.floatValue();
        } else if (expectedType == short.class || expectedType == Short.class) {
            return number.shortValue();
        } else if (expectedType == BigDecimal.class) {
            return new BigDecimal(number.toString());
        } else if (expectedType == BigInteger.class) {
            return new BigInteger(number.toString());
        }
        throw new IllegalStateException("ExpectedType is Not a number");
    }

    private Response getResponse(Request request) {
        String buffer = transporter.sendAndReceive(marshaller.marshall(request));
        logger.info(buffer);
        @SuppressWarnings("unchecked")
        Map<String, ?> response = marshaller.unmarshall(buffer, Map.class);
        return new Response(response);
    }

    private void callAndReturnNone(Request request) {
        transporter.sendAndForget(marshaller.marshall(request));
    }

    private Object callAndPrepareForFuture(Object res, java.lang.reflect.Method method) {
        Type paramType = ((ParameterizedType) method.getGenericReturnType()).getActualTypeArguments()[0];
        if (Number.class.isAssignableFrom((Class) paramType)) {
            return enforceCorrectNumberType((Number) res, (Class) paramType);
        } else {
            return res;
        }
    }

    @Override
    public final Object invoke(final Object o,
                               final java.lang.reflect.Method method,
                               final Object[] args) throws Throwable {
        final String methodName = method.getName();

        // handle essential Object methods
        if ("hashCode".equals(methodName)) {
            return uuid.hashCode();
        } else if ("equals".equals(methodName)) {
            return (o == args[0] ? Boolean.TRUE : Boolean.FALSE);
        } else if ("toString".equals(methodName)) {
            return o.getClass().getName() + '@' + Integer.toHexString(uuid.hashCode());
        }

        MetaDataCache mdc = mdCache.putIfAbsent(method, MetaDataCache.create(method, args));
        if (mdc == null) {
            mdc = mdCache.get(method);
        }

        final Request request = Request.method(mdc.hasModifiedName() ? mdc.getModifiedName() : methodName);
        if (mdc.hasNamedArguments()) {
            request.params(mdc.getNamedArguments(args));
        } else {
            request.params(mdc.getPositionalArguments(args));
        }

        final Class<?> returnType = method.getReturnType();

        final Callable<Object> dispatcher = new Callable<Object>() {

            @Override
            public Object call() throws Exception {
                if (returnType == Void.TYPE) {
                    callAndReturnNone(request);
                    return null;
                }

                final Response response = getResponse(request);
                if (response.containsError()) {
                    throw new ResponseError(response.getErrorCode(), response.getErrorMessage());
                }

                final Object result = response.getResult();
                if (returnType == getFutureClass()) {
                    return callAndPrepareForFuture(result, method);
                } else if (result instanceof Number) { // if it's number box the specialized value
                    return enforceCorrectNumberType((Number) result, returnType);
                } else {
                    return result;
                }
            }
        };

        if (returnType != Void.TYPE) {
            request.id(
                    mdc.hasIdInSignature()
                            ? (Integer) args[args.length - 1]
                            : idGenerator.incrementAndGet());
        }

        if (returnType == getFutureClass()) {
            return getExecutorService().submit(dispatcher);
        } else {
            return dispatcher.call();
        }
    }

    private static class MetaDataCache {

        private Map<Integer, String> namedArguments;
        private String modifiedName;
        private boolean isIdInSignature;

        MetaDataCache(Map<Integer, String> namedArguments, String modifiedName, boolean isIdInSignature) {
            this.namedArguments = namedArguments;
            this.modifiedName = modifiedName;
            this.isIdInSignature = isIdInSignature;
        }

        private static MetaDataCache create(Method method, Object[] args) {
            Map<Integer, String> argumentMapping = new HashMap<Integer, String>();
            String modifiedName = null;
            boolean idInSignature = false;

            if (args != null) {
                Annotation[][] parameterAnnotations = method.getParameterAnnotations();
                for (int i = 0, j = args.length - 1; i < args.length; i++) {
                    Annotation[] annotations = parameterAnnotations[i];
                    for (Annotation annotation : annotations) {
                        if (annotation instanceof Name) {
                            argumentMapping.put(i, ((Name) annotation).value());
                        } else if (annotation instanceof Id && i == j && args[i] instanceof Integer) {
                            idInSignature = true;
                        }
                    }
                }

                if (idInSignature && (argumentMapping.size() != (args.length - 1))) {
                    throw new IllegalStateException(
                            "Incorrectly annotated method. If @Name is used, it has to be used for all arguments. Except to that rule is @Id annotated end argument");
                }
            }

            for (Annotation annotation : method.getAnnotations()) {
                if (annotation instanceof cz.snappyapps.snappyrpc.Method) {
                    modifiedName = ((cz.snappyapps.snappyrpc.Method) annotation).value();
                    break;
                }
            }

            return new MetaDataCache(argumentMapping, modifiedName, idInSignature);
        }

        boolean hasNamedArguments() {
            return namedArguments.size() > 0;
        }

        boolean hasIdInSignature() {
            return isIdInSignature;
        }

        boolean hasModifiedName() {
            return modifiedName != null;
        }

        String getModifiedName() {
            return modifiedName;
        }

        Map<String, Object> getNamedArguments(Object[] args) {
            HashMap<String, Object> requestArguments = new HashMap<String, Object>();
            for (int i = 0, len = hasIdInSignature() ? args.length - 1 : args.length;
                 i < len;
                 i++) {
                requestArguments.put(namedArguments.get(i), args[i]);
            }
            return requestArguments;
        }

        Object[] getPositionalArguments(Object[] args) {
            return hasIdInSignature() ? Arrays.copyOf(args, args.length - 1) : args;
        }

        @Override
        public String toString() {
            final StringBuilder sb = new StringBuilder("MetaDataCache{");
            sb.append("namedArguments=").append(namedArguments);
            sb.append(", modifiedName='").append(modifiedName).append('\'');
            sb.append(", isIdInSignature=").append(isIdInSignature);
            sb.append('}');
            return sb.toString();
        }
    }
}
