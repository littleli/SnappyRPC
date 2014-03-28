package cz.snappyapps.snappyrpc;

import java.util.*;

/**
 * @author Ales Najmann
 *         <p/>
 *         http://www.snappyrpc.org/specification#error_object
 */
public final class Request extends LinkedHashMap<String, Object> {

    static Request empty() {
        return (new Request()).andPut("jsonrpc", "2.0");
    }

    public static Request method(String methodName) {
        return empty().andPut("method", methodName);
    }

    public Request andPut(String field, Object value) {
        put(field, value);
        return this;
    }

    public Request id(int id) {
        return andPut("id", id);
    }

    public Request param(Object value) {
        return andPut("params", Arrays.asList(value));
    }

    public Request params(Map<String, ?> namedParams) {
        return namedParams != null ? andPut("params", namedParams) : this;
    }

    public Request params(List<?> positionalParams) {
        return positionalParams != null ? andPut("params", positionalParams) : params(new ArrayList<Object>());
    }

    public Request params(Object[] positionalParams) {
        return positionalParams != null ? params(Arrays.asList(positionalParams)) : params(new ArrayList<Object>());
    }

    public boolean isNotification() {
        return !containsKey("id");
    }

    public static Request create(String methodName, Object value) {
        return method(methodName).param(value);
    }

    public static Request create(String methodName, Object value, int id) {
        return create(methodName, value).id(id);
    }

    public static Request create(String methodName, Object[] args) {
        return method(methodName).params(args);
    }

    public static Request create(String methodName, Object[] args, int id) {
        return create(methodName, args).id(id);
    }

    public static Request create(String methodName, Map<String, ?> args) {
        return method(methodName).params(args);
    }

    public static Request create(String methodName, Map<String, ?> args, int id) {
        return create(methodName, args).id(id);
    }
}
