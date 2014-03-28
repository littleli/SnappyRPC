package cz.snappyapps.snappyrpc.client;

import com.google.common.util.concurrent.ListenableFuture;
import cz.snappyapps.snappyrpc.Id;
import cz.snappyapps.snappyrpc.Method;
import cz.snappyapps.snappyrpc.Name;
import cz.snappyapps.snappyrpc.Response;
import cz.snappyapps.snappyrpc.client.marshaller.Marshaller;
import cz.snappyapps.snappyrpc.client.transporter.StubTransporter;
import cz.snappyapps.snappyrpc.client.transporter.Transporter;
import org.junit.Test;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.atomic.AtomicInteger;

import static org.junit.Assert.*;

public abstract class AbstractRpcTest {

    AtomicInteger generator = new AtomicInteger(0);
    StubTransporter sender = new StubTransporter();
    ExecutorService executorService = Executors.newFixedThreadPool(1);

    protected SimpleTestService service;

    public void setup() {
        RpcFactory factory = new RpcFactory(getSender(), getMarshaller(), executorService, getGenerator());
        service = factory.create(SimpleTestService.class);
    }

    public AtomicInteger getGenerator() {
        return generator;
    }

    public abstract Marshaller getMarshaller();

    public static interface SimpleTestService {

        @Method("subtract-dummy")
        int subtractDummy(int a, int b);

        boolean even(int n);

        Boolean odd(int n);

        // primitives setup
        byte subtractBytes(byte a, byte b);

        int subtractInts(int a, int b);

        short subtractShorts(short a, short b);

        long subtractLongs(long a, long b);

        float subtractFloats(float a, float b);

        double subtractDouble(double a, double b);

        // same but with wrappers
        Byte subtractBytesW(byte a, byte b);

        Integer subtractIntsW(int a, int b);

        Short subtractShortsW(short a, short b);

        Long subtractLongsW(long a, long b);

        Float subtractFloatsW(float a, float b);

        Double subtractDoubleW(double a, double b);

        int subtract(@Name("minuend") int a, @Name("subtahend") int b);

        int subtract(@Name("minuend") int a, @Name("subtahend") int b, @Id int id);

        Future<Long> veryExpensiveSubtract(long i, long j);

        ListenableFuture<Long> veryExpensiveSubtractToListen(long i, long j);

        void subtractNotify(int a, int b);
    }

    static Map<String, ?> createError(int code, String message) {
        HashMap<String, Object> error = new HashMap<String, Object>();
        error.put("code", code);
        error.put("message", message);
        return error;
    }

    @Test
    public void testBaseObjectMethods() {
        assertTrue(service.toString().contains(Integer.toHexString(service.hashCode())));
        assertEquals(service, service);
        assertNotEquals(service, new Object());
    }

    @Test
    public void testCallWithOverridenMethodName() {
        Response r1 = new Response();
        r1.put("jsonrpc", "2.0");
        r1.put("result", 79);
        generator.set(1);
        r1.put("id", generator.get());
        sender.setStringToReturn(getMarshaller().marshall(r1));
        assertEquals(79, service.subtractDummy(100, 21));
    }

    @Test
    public void testCallWithPositionalArguments() {
        Response r2 = new Response();
        r2.put("jsonrpc", "2.0");
        r2.put("result", 80);
        generator.set(2);
        r2.put("id", generator.get());
        sender.setStringToReturn(getMarshaller().marshall(r2));

        assertEquals((byte) 80, service.subtractBytes((byte) 101, (byte) 21));
        assertEquals(new Byte("80"), service.subtractBytesW(new Byte("101"), new Byte("21")));

        assertEquals((short) 80, service.subtractShorts((short) 101, (short) 21));
        assertEquals(new Short("80"), service.subtractShortsW((short) 101, (short) 21));

        assertEquals(80, service.subtractInts(101, 21));
        assertEquals(new Integer("80"), service.subtractIntsW(new Integer("101"), new Integer("21")));

        assertEquals(80L, service.subtractLongs(101, 21));
        assertEquals(new Long("80"), service.subtractLongsW(new Long("101"), new Long("21")));

        assertEquals(80d, service.subtractDouble(101d, 21d), 0.001);
        assertEquals(new Double("80"), service.subtractDoubleW(101d, 21d), 0.001);

        assertEquals(80f, service.subtractFloats(101f, 21f), 0.001);
        assertEquals(new Float("80"), service.subtractFloatsW(101f, 21f), 0.001);

        r2.put("result", Boolean.TRUE);
        sender.setStringToReturn(getMarshaller().marshall(r2));
        assertEquals(true, service.even(2));

        r2.put("result", Boolean.FALSE);
        sender.setStringToReturn(getMarshaller().marshall(r2));
        assertEquals(false, service.odd(2));
    }

    @Test
    public void testCallWithNamedArguments() {
        Response r3 = new Response();
        r3.put("jsonrpc", "2.0");
        r3.put("result", 81);
        generator.set(3);
        r3.put("id", generator.get());
        sender.setStringToReturn(getMarshaller().marshall(r3));
        assertEquals(81, service.subtract(102, 21));
    }

    @Test
    public void testCallWithNamedArgumentsAndExplicitlyGivenIdTag() {
        Response r4 = new Response();
        r4.put("jsonrpc", "2.0");
        r4.put("result", 82);
        r4.put("id", 1000);
        sender.setStringToReturn(getMarshaller().marshall(r4));
        assertEquals(82, service.subtract(102, 20, 1000));
    }

    @Test
    public void testCallReturningFutureOfLong() throws ExecutionException, InterruptedException {
        Response r5 = new Response();
        r5.put("jsonrpc", "2.0");
        r5.put("result", 80L);
        generator.set(5);
        r5.put("id", generator.get());
        sender.setStringToReturn(getMarshaller().marshall(r5));
        Future<Long> flong = service.veryExpensiveSubtract(102L, 22L);
        assertEquals(new Long(80), flong.get());
    }


    @Test
    public void testNotifyCall() {
        Response r6 = new Response();
        r6.put("jsonrpc", "2.0");
        sender.setStringToReturn(getMarshaller().marshall(r6));
        service.subtractNotify(10, 1);
        service.subtractNotify(10, -1);
    }

    @Test(expected = ResponseError.class)
    public void testCallWithInvalidArgumentsGiven() {
        Response r7 = new Response();
        r7.put("jsonrpc", "2.0");
        r7.put("error", createError(-32602, "Invalid params"));
        generator.set(6);
        r7.put("id", generator.get());
        sender.setStringToReturn(getMarshaller().marshall(r7));
        try {
            service.subtract(100, 1);
        } catch (ResponseError err) {
            assertEquals(-32602, err.getCode());
            throw err;
        }
    }

    public Transporter getSender() {
        return sender;
    }
}
