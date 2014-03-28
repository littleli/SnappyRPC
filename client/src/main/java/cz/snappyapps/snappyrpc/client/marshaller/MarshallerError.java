package cz.snappyapps.snappyrpc.client.marshaller;

public class MarshallerError extends RuntimeException {

    public MarshallerError(Throwable cause) {
        super(cause);
    }

    public MarshallerError(String msg) {
        super(msg);
    }

    public MarshallerError(String msg, Throwable cause) {
        super(msg, cause);
    }
}