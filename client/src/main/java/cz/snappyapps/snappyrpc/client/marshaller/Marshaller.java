package cz.snappyapps.snappyrpc.client.marshaller;

/**
 * @author Ales Najmann
 */
public interface Marshaller {

    /**
     * Unmarshall given string to the object
     *
     * @param str string to contain representation
     * @param <T> type token
     * @return instance of the object created out of str input
     * @throws MarshallerError
     */
    <T> T unmarshall(String str, Class<T> typeToken);

    /**
     * Marshall given object to json
     *
     * @param object object to be serialised to given representation
     * @return string containing representation
     * @throws MarshallerError
     */
    String marshall(Object object);
}