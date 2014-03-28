package cz.snappyapps.snappyrpc.client;

import com.google.common.util.concurrent.ListeningExecutorService;
import com.google.common.util.concurrent.MoreExecutors;
import cz.snappyapps.snappyrpc.client.transporter.Transporter;
import cz.snappyapps.snappyrpc.client.marshaller.Marshaller;

import java.lang.reflect.Proxy;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.atomic.AtomicInteger;

public final class RpcListeningFactory {

    private final Transporter transporter;
    private final Marshaller marshaller;
    private final ListeningExecutorService listeningExecutorService;
    private final AtomicInteger generator;

    public RpcListeningFactory(Transporter transporter, Marshaller marshaller, ListeningExecutorService listeningExecutorService, AtomicInteger generator) {
        this.transporter = transporter;
        this.marshaller = marshaller;
        this.listeningExecutorService = listeningExecutorService;
        this.generator = generator;
    }

    public RpcListeningFactory(Transporter transporter, Marshaller marshaller) {
        this(transporter, marshaller, MoreExecutors.sameThreadExecutor());
    }

    public RpcListeningFactory(Transporter transporter, Marshaller marshaller, ExecutorService executorService) {
        this(transporter, marshaller, MoreExecutors.listeningDecorator(executorService), new AtomicInteger(0));
    }

    public RpcListeningFactory(Transporter transporter, Marshaller marshaller, ListeningExecutorService executorService) {
        this(transporter, marshaller, executorService, new AtomicInteger(0));
    }

    public <T> T create(Class<T> service) {
        RpcListeningInvocationHandler handler = new RpcListeningInvocationHandler();
        handler.setTransporter(transporter);
        handler.setMarshaller(marshaller);
        handler.setExecutorService(listeningExecutorService);
        handler.setIdGenerator(generator);
        return service.cast(Proxy.newProxyInstance(service.getClassLoader(),
                new Class<?>[]{service},
                handler));
    }
}
