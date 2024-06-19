package eu.phaf.stateman;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.util.List;

public class JsonDeserializer {
    private static final ObjectMapper objectMapper = new ObjectMapper();

    public static String deserialize(Object o) {
        try {
            return objectMapper.writeValueAsString(o);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }

    public static <T> T serialize(String o, TypeReference<T> theClass) {
        try {
            return objectMapper.readValue(o, theClass);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }
}
