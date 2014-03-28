package cz.snappyapps.snappyrpc.client;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;

class RpcBasicInvocationHandler extends RpcInvocationHandler {

    private ExecutorService executorService;

    public void setExecutorService(ExecutorService executorService) {
        this.executorService = executorService;
    }

    @Override
    protected ExecutorService getExecutorService() {
        return executorService;
    }

    @Override
    protected Class<? extends Future> getFutureClass() {
        return Future.class;
    }
}
