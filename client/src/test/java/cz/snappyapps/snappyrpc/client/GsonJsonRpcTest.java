package cz.snappyapps.snappyrpc.client;

import cz.snappyapps.snappyrpc.client.marshaller.GsonMarshaller;

public class GsonJsonRpcTest extends AbstractRpcTest {

    GsonMarshaller serializer = new GsonMarshaller();

    public GsonMarshaller getMarshaller() {
        return serializer;
    }

    public GsonJsonRpcTest() {
        setup();
    }
}
