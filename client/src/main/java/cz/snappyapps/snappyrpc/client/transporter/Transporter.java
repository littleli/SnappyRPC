package cz.snappyapps.snappyrpc.client.transporter;

public interface Transporter {

    void sendAndForget(CharSequence data);

    String sendAndReceive(CharSequence data);
}
