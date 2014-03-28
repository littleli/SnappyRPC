package cz.snappyapps.snappyrpc.client.marshaller;

import com.thoughtworks.xstream.XStream;

public class XStreamMarshaller implements Marshaller {

    private final XStream xstream;

    public XStreamMarshaller(XStream xstream) {
        this.xstream = xstream;
    }

    public XStreamMarshaller() {
        this(new XStream());
    }

    @Override
    public <T> T unmarshall(String str, Class<T> aClass) {
        try {
            Object value = xstream.fromXML(str);
            return aClass.cast(value);
        } catch (Exception e) {
            throw new MarshallerError(e);
        }
    }

    @Override
    public String marshall(Object o) {
        try {
            return xstream.toXML(o);
        } catch (Exception e) {
            throw new MarshallerError(e);
        }
    }
}
