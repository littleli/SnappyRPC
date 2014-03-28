package cz.snappyapps.snappyrpc.client;

import cz.snappyapps.snappyrpc.client.marshaller.JacksonMarshaller;
import cz.snappyapps.snappyrpc.client.marshaller.Marshaller;

public class JacksonJsonListeningRpcTest extends AbstractListeningRpcTest {

    Marshaller marshaller = new JacksonMarshaller();

    public JacksonJsonListeningRpcTest() {
        setup();
    }

    @Override
    public Marshaller getMarshaller() {
        return marshaller;
    }
}
