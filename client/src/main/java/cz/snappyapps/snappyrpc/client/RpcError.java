package cz.snappyapps.snappyrpc.client;

/**
 * @author Ales Najmann
 *
 * This is general base class for framework errors. It's runtime exception, so no explicit handling
 * is needed if you don't want to.
 */
public class RpcError extends RuntimeException {

    public RpcError(String s) {
        super(s);
    }

    public RpcError(String s, Throwable throwable) {
        super(s, throwable);
    }

    public RpcError(Throwable throwable) {
        super(throwable);
    }
}
