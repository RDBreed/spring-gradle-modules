package eu.phaf.stateman;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.TreeNode;
import com.fasterxml.jackson.databind.BeanProperty;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.deser.ContextualDeserializer;
import com.fasterxml.jackson.databind.jsontype.TypeDeserializer;
import com.fasterxml.jackson.databind.node.TextNode;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class EscapedJsonDeserializer extends JsonDeserializer<Object> implements ContextualDeserializer {
    private final Map<JavaType, JsonDeserializer<Object>> cachedDeserializers = new HashMap<>();

    @Override
    public ParameterClassNameAndValue deserialize(JsonParser p, DeserializationContext ctxt) throws IOException, JsonProcessingException {
        throw new IllegalArgumentException("EscapedJsonDeserializer should delegate deserialization for concrete class");

    }

    @Override
    public JsonDeserializer<?> createContextual(DeserializationContext ctxt, BeanProperty property) throws JsonMappingException {
        JavaType type = (ctxt.getContextualType() != null) ?
                ctxt.getContextualType() : property.getMember().getType();
        return cachedDeserializers.computeIfAbsent(type, (a) -> new InnerDeserializer(type));
    }

    private class InnerDeserializer extends JsonDeserializer<Object> {
        private final JavaType javaType;

        private InnerDeserializer(JavaType javaType) {
            this.javaType = javaType;
        }

        @Override
        public Object deserialize(JsonParser p, DeserializationContext ctxt) throws IOException, JsonProcessingException {
            String str = p.readValueAs(String.class);
            TreeNode root = ((ObjectMapper) p.getCodec()).readTree(str);
            return getObject(p, root, str);
        }

        private Object getObject(JsonParser p, TreeNode root, String str) throws JsonProcessingException {
            if (root.isObject()) {
                Class clz;
                try {
                    clz = Class.forName(((TextNode) root.get("@class")).asText());
                    Object newJsonNode = p.getCodec().treeToValue(root, clz);
                    return newJsonNode;
                } catch (ClassNotFoundException e) {
                    throw new RuntimeException(e);
                }
            } else if(root.isArray() && root.get(0) != null){
                List<Object> list = new ArrayList<>();
                for (int i = 0; i < root.size(); i++) {
                    list.add(getObject(p, root.get(i), str));
                }
                return list;
            } else {
                return ((ObjectMapper) p.getCodec()).readValue(str, javaType);
            }
        }

        @Override
        public Object deserializeWithType(JsonParser p, DeserializationContext ctxt, TypeDeserializer typeDeserializer)
                throws IOException {

            String str = p.readValueAs(String.class);


            TreeNode root = ((ObjectMapper) p.getCodec()).readTree(str);
            Class clz;
            try {
                clz = Class.forName(((TextNode) root.get("@class")).asText());
                Object newJsonNode = p.getCodec().treeToValue(root, clz);
                return newJsonNode;
            } catch (ClassNotFoundException e) {
                throw new RuntimeException(e);
            }
        }
    }
}
