package eu.phaf.stateman;

import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.jsontype.TypeSerializer;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.fasterxml.jackson.databind.node.TextNode;
import com.fasterxml.jackson.databind.ser.std.StdSerializer;

import java.io.IOException;
import java.io.StringWriter;
import java.util.Collection;

class EscapedJsonSerializer extends StdSerializer<Object> {
    public EscapedJsonSerializer() {
        super((Class<Object>) null);
    }

    @Override
    public void serialize(Object all, JsonGenerator gen, SerializerProvider provider) throws IOException {
        StringWriter str = new StringWriter();
        JsonGenerator tempGen = new JsonFactory().setCodec(gen.getCodec()).createGenerator(str);
        Object value = all;
        if (value instanceof Collection || value.getClass().isArray()) {
            tempGen.writeStartArray();
            if (value instanceof Collection) {
                for (Object it : (Collection) value) {
                    writeTree(gen, it, tempGen);
                }
            } else if (value.getClass().isArray()) {
                for (Object it : (Object[]) value) {
                    writeTree(gen, it, tempGen);
                }
            }
            tempGen.writeEndArray();
        } else {
            writeTree(gen, value, tempGen);
        }
        tempGen.flush();
        gen.writeString(str.toString());
    }

    @Override
    public void serializeWithType(Object value, JsonGenerator gen, SerializerProvider serializers, TypeSerializer typeSer) throws IOException {
        StringWriter str = new StringWriter();
        JsonGenerator tempGen = new JsonFactory().setCodec(gen.getCodec()).createGenerator(str);
        writeTree(gen, value, tempGen);
        tempGen.flush();
        gen.writeString(str.toString());
    }

    private void writeTree(JsonGenerator gen, Object it, JsonGenerator tempGen) throws IOException {
        JsonNode tree = ((ObjectMapper) gen.getCodec()).valueToTree(it);
        if(tree instanceof ObjectNode objectNode) {
            objectNode.set("@class", new TextNode(it.getClass().getName()));
            tempGen.writeTree(objectNode);
        } else {
            tempGen.writeTree(tree);
        }
    }
}
