package cz.snappyapps.snappyrpc.client.transporter;

public class TransportError extends RuntimeException {

    public TransportError(String msg) {
        super(msg);
    }

    public TransportError(String msg, Throwable throwable) {
        super(msg, throwable);
    }

    public TransportError(Throwable throwable) {
        super(throwable);
    }
}
