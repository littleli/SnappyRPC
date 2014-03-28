package cz.snappyapps.snappyrpc.client;

import cz.snappyapps.snappyrpc.client.marshaller.EdnMarshaller;
import cz.snappyapps.snappyrpc.client.marshaller.Marshaller;

public class EdnRpcTest extends AbstractRpcTest {

    EdnMarshaller serializer = new EdnMarshaller();

    public EdnRpcTest() {
        setup();
    }

    @Override
    public Marshaller getMarshaller() {
        return serializer;
    }
}
