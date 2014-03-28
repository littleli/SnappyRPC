package cz.snappyapps.snappyrpc.client.marshaller;

import com.google.gson.Gson;
import com.google.gson.JsonIOException;
import com.google.gson.JsonSyntaxException;

/**
 * Warning: this class IS NOT thread-safe, because simply Gson object is not thead-safe by default
 */
public final class GsonMarshaller implements Marshaller {

    private final Gson gson;

    /**
     * This might be possibly expensive, since for every construction new Gson object is created
     */
    public GsonMarshaller() {
        this(new Gson());
    }

    /**
     * @param gson Gson instance configured outside of this class
     * @throws java.lang.NullPointerException
     */
    public GsonMarshaller(final Gson gson) {
        if (gson == null) {
            throw new NullPointerException("Gson instance required");
        }
        this.gson = gson;
    }

    @Override
    public <T> T unmarshall(String str, Class<T> aClass) {
        try {
            return gson.fromJson(str, aClass);
        } catch (JsonSyntaxException e) {
            throw new MarshallerError(e);
        }
    }

    @Override
    public String marshall(Object o) {
        try {
            return gson.toJson(o);
        } catch (JsonIOException e) {
            throw new MarshallerError(e);
        }
    }
}
