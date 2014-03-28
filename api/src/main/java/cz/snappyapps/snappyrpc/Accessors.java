package cz.snappyapps.snappyrpc;

import java.util.HashMap;
import java.util.Map;

public final class Accessors {

    private Accessors() {
    }

    @SuppressWarnings("unchecked")
    public static Object getter(final Map<String, Object> objectMap,
                                final String path,
                                final boolean createIfNotExist,
                                final Object implicit) {
        String[] parts = path.split("\\.");
        Map<String, Object> subObjectMap = objectMap;
        Object o = null;
        for (int i = 0, j = parts.length - 1; i < parts.length; i++) {
            String part = parts[i];
            o = subObjectMap.get(part);
            if (o instanceof Map) {
                subObjectMap = (Map<String, Object>) o;
            } else if (o == null) {
                if (createIfNotExist) {
                    subObjectMap.put(part, i == j ? implicit : new HashMap<String, Object>());
                } else {
                    return subObjectMap.containsKey(part) ? null : implicit;
                }
            } else {
                return o;
            }
        }
        return implicit;
    }

    public static Object getter(final Map<String, Object> objectMap, final String path) {
        return getter(objectMap, path, false, null);
    }

    public static Number getNumber(Map<String, Object> objectMap, final String path) {
        Object o = getter(objectMap, path);
        if (o instanceof Number) {
            return (Number) o;
        } else if (o instanceof String) {
            return Double.valueOf((String) o);
        }
        throw new IllegalArgumentException("Path '" + path + "' does not contain anything which might look like number");
    }

    public static int getInt(Map<String, Object> objectMap, final String path) {
        Object o = getter(objectMap, path);
        if (o instanceof Number) {
            return ((Number) o).intValue();
        } else if (o instanceof String) {
            return Integer.parseInt((String) o, 10);
        }
        throw new IllegalArgumentException("Path '" + path + "' does not contain anything looking like int");
    }

}
