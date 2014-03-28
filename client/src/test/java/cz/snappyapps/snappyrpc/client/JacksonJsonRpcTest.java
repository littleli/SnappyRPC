package cz.snappyapps.snappyrpc.client;

import cz.snappyapps.snappyrpc.client.marshaller.JacksonMarshaller;
import cz.snappyapps.snappyrpc.client.marshaller.Marshaller;

public class JacksonJsonRpcTest extends AbstractRpcTest {

    Marshaller marshaller = new JacksonMarshaller();

    public JacksonJsonRpcTest() {
        setup();
    }

    @Override
    public Marshaller getMarshaller() {
        return marshaller;
    }
}
