package cz.snappyapps.snappyrpc;

import java.util.LinkedHashMap;
import java.util.Map;

import static cz.snappyapps.snappyrpc.Accessors.getInt;
import static cz.snappyapps.snappyrpc.Accessors.getter;

/**
 * @author Ales Najmann
 *         <p/>
 *         http://www.snappyrpc.org/specification#response_object
 */
public final class Response extends LinkedHashMap<String, Object> {

    public Response(int initialCapacity, float loadFactor) {
        super(initialCapacity, loadFactor);
    }

    public Response(int initialCapacity) {
        super(initialCapacity);
    }

    public Response() {
        super();
    }

    public Response(Map<? extends String, ?> m) {
        super(m);
    }

    public Response(int initialCapacity, float loadFactor, boolean accessOrder) {
        super(initialCapacity, loadFactor, accessOrder);
    }

    public boolean containsError() {
        return null != get("error");
    }

    public Object getResult() {
        return get("result");
    }

    public int getErrorCode() {
        return getInt(this, "error.code");
    }

    public String getErrorMessage() {
        return (String) getter(this, "error.message");
    }
}


