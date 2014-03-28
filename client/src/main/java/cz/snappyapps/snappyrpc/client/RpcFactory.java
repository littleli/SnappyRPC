package cz.snappyapps.snappyrpc.client;

import cz.snappyapps.snappyrpc.client.transporter.Transporter;
import cz.snappyapps.snappyrpc.client.marshaller.Marshaller;

import java.lang.reflect.Proxy;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * @author Ales Najmann
 *         <p/>
 *         Created by littleli on 13.01.14.
 */
public final class RpcFactory {

    private final Transporter transporter;
    private final Marshaller marshaller;
    private final ExecutorService executorService;
    private final AtomicInteger generator;

    public RpcFactory(Transporter transporter, Marshaller marshaller, ExecutorService executorService, AtomicInteger generator) {
        this.transporter = transporter;
        this.marshaller = marshaller;
        this.executorService = executorService;
        this.generator = generator;
    }

    public RpcFactory(Transporter transporter, Marshaller marshaller) {
        this(transporter, marshaller, Executors.newSingleThreadExecutor());
    }

    public RpcFactory(Transporter transporter, Marshaller marshaller, ExecutorService executorService) {
        this(transporter, marshaller, executorService, new AtomicInteger(0));
    }

    public <T> T create(Class<T> service) {
        RpcBasicInvocationHandler handler = new RpcBasicInvocationHandler();
        handler.setTransporter(transporter);
        handler.setMarshaller(marshaller);
        handler.setExecutorService(executorService);
        handler.setIdGenerator(generator);
        return service.cast(Proxy.newProxyInstance(service.getClassLoader(),
                new Class<?>[]{service},
                handler));
    }
}
