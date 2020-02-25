package ure.areas.gen;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.ObjectCodec;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.reflections.Reflections;
import org.reflections.scanners.SubTypesScanner;

import java.io.IOException;
import java.lang.reflect.Field;
import java.util.Set;

public class LandscaperDeserializer extends JsonDeserializer<ULandscaper> {

    private ObjectMapper objectMapper;

    private Reflections reflections = new Reflections("ure", new SubTypesScanner());
    private Set<Class<? extends ULandscaper>> landscaperClasses = reflections.getSubTypesOf(ULandscaper.class);

    public LandscaperDeserializer(ObjectMapper mapper) {
        objectMapper = mapper;
    }

    @Override
    public ULandscaper deserialize(JsonParser parser, DeserializationContext context) throws IOException {
        ObjectCodec codec = parser.getCodec();
        JsonNode node = codec.readTree(parser);
        JsonNode typeNode = node.get("type");
        String type = (typeNode != null && !typeNode.isNull()) ? node.get("type").asText() : null;
        Class<? extends ULandscaper> landscaperClass = classForType(type);
        return objectMapper.treeToValue(node, landscaperClass);
    }

    private Class<? extends ULandscaper> classForType(String type) {
        if (type == null || type.equals("")) {
            throw new RuntimeException("ULandscaper JSON must specify a valid type in their type field that matches the TYPE field of a ULandscaper subclass");
        }
        try {
            for (Class<? extends ULandscaper> actorClass : landscaperClasses) {
                Field typeField = actorClass.getField("TYPE");
                String typeValue = (String) typeField.get(null);
                if (type.equals(typeValue)) {
                    return actorClass;
                }
            }
            throw new RuntimeException("No ULandscaper class of type '" + type + "' was found in the classpath");
        } catch (NoSuchFieldException e) {
            throw new RuntimeException("All subclasses of ULandscaper must define a static TYPE field", e);
        } catch (IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }
}
