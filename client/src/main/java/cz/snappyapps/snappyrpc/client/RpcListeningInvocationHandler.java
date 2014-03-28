package cz.snappyapps.snappyrpc.client;

import com.google.common.util.concurrent.ListenableFuture;
import com.google.common.util.concurrent.ListeningExecutorService;

import java.util.concurrent.Future;

final class RpcListeningInvocationHandler extends RpcInvocationHandler {

    private ListeningExecutorService executorService;

    public void setExecutorService(ListeningExecutorService executorService) {
        this.executorService = executorService;
    }

    @Override
    protected ListeningExecutorService getExecutorService() {
        return executorService;
    }

    @Override
    protected Class<? extends Future> getFutureClass() {
        return ListenableFuture.class;
    }
}
