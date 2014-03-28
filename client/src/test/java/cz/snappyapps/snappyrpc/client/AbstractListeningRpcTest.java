package cz.snappyapps.snappyrpc.client;

import com.google.common.util.concurrent.ListenableFuture;
import com.google.common.util.concurrent.MoreExecutors;
import cz.snappyapps.snappyrpc.Response;
import cz.snappyapps.snappyrpc.client.marshaller.Marshaller;
import cz.snappyapps.snappyrpc.client.transporter.StubTransporter;
import cz.snappyapps.snappyrpc.client.transporter.Transporter;
import org.junit.Test;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.atomic.AtomicInteger;

import static org.junit.Assert.assertEquals;

public abstract class AbstractListeningRpcTest {

    AtomicInteger generator = new AtomicInteger(0);
    StubTransporter sender = new StubTransporter();

    protected SimpleTestService service;

    public void setup() {
        RpcListeningFactory factory = new RpcListeningFactory(getSender(), getMarshaller(), MoreExecutors.sameThreadExecutor(), getGenerator());
        service = factory.create(SimpleTestService.class);
    }

    public AtomicInteger getGenerator() {
        return generator;
    }

    public abstract Marshaller getMarshaller();

    public Transporter getSender() {
        return sender;
    }

    public static interface SimpleTestService {

        ListenableFuture<Long> veryExpensiveSubtractToListen(long i, long j);
    }

    @Test
    public void testCallReturningListenableFutureOfLong() throws ExecutionException, InterruptedException {
        Response r8 = new Response();
        r8.put("jsonrpc", "2.0");
        r8.put("result", 80L);
        generator.set(8);
        r8.put("id", generator.get());
        sender.setStringToReturn(getMarshaller().marshall(r8));
        ListenableFuture<Long> flong = service.veryExpensiveSubtractToListen(102L, 22L);
        flong.addListener(new Runnable() {
            @Override
            public void run() {
                System.out.println("Wow!");
            }
        }, MoreExecutors.sameThreadExecutor());
        assertEquals(new Long(80), flong.get());
    }
}
