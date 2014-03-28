package cz.snappyapps.snappyrpc.client;

public class ResponseError extends RuntimeException {

    private int code;

    public ResponseError(int code, String message) {
        super(message);
        this.code = code;
    }

    public int getCode() {
        return code;
    }
}
