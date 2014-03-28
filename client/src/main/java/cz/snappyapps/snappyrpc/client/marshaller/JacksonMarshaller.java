package cz.snappyapps.snappyrpc.client.marshaller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;

/**
 * This class is most-likely thread safe. Since JacksonJson is thread safe.
 *
 * @author Ales Najmann
 */
public final class JacksonMarshaller implements Marshaller {

    private final ObjectMapper mapper;

    public JacksonMarshaller() {
        this(new ObjectMapper());
    }

    public JacksonMarshaller(final ObjectMapper mapper) {
        if (mapper == null) {
            throw new NullPointerException("ObjectMapper instance required");
        }
        this.mapper = mapper;
    }

    @Override
    public <T> T unmarshall(String str, Class<T> t) {
        try {
            return mapper.readValue(str, t);
        } catch (IOException e) {
            throw new MarshallerError(e);
        }
    }

    @Override
    public String marshall(Object o) {
        try {
            return mapper.writeValueAsString(o);
        } catch (JsonProcessingException e) {
            throw new MarshallerError(e);
        }
    }
}