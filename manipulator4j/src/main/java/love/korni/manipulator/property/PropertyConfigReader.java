package love.korni.manipulator.property;

import love.korni.manipulator.core.gear.file.FileManager;
import love.korni.manipulator.core.gear.file.reader.ClasspathReader;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;

import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

/**
 * @author Sergei_Kornilov
 */
class PropertyConfigReader {

    private final static String BASE_MANIPULATOR = ClasspathReader.CLASSPATH + "base-manipulator.yml";
    private final static String MANIPULATOR = ClasspathReader.CLASSPATH + "manipulator.yml";

    private static final String PROFILE_KEY = "profile-";

    private final FileManager fileManager;
    private final ObjectMapper objectMapper;
    private final JsonNode propertyNode;

    public PropertyConfigReader(FileManager fileManager, List<String> propertyProfiles) {
        this(fileManager, new ObjectMapper(new YAMLFactory()), propertyProfiles);
    }

    public PropertyConfigReader(FileManager fileManager, ObjectMapper objectMapper, List<String> propertyProfiles) {
        this.fileManager = fileManager;
        this.objectMapper = objectMapper;
        this.objectMapper.setPropertyNamingStrategy(PropertyNamingStrategies.SNAKE_CASE);
        this.objectMapper.findAndRegisterModules();
        this.propertyNode = read(propertyProfiles);
    }

    public JsonNode getPropertyNode() {
        return propertyNode;
    }

    private JsonNode read(List<String> propertyProfiles) {
        JsonNode node = readJsonNode(BASE_MANIPULATOR);

        if (fileManager.fileExists(MANIPULATOR)) {
            merge(node, readJsonNode(MANIPULATOR));
        }

        profilesHandle(node, propertyProfiles);

        return node;
    }

    private JsonNode readJsonNode(String filename) {
        try {
            InputStream inputStream = fileManager.readFile(filename);
            return objectMapper.readTree(inputStream);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private void merge(JsonNode mainNode, JsonNode updateNode) {
        Iterator<String> fieldNames = updateNode.fieldNames();
        while (fieldNames.hasNext()) {

            String fieldName = fieldNames.next();
            JsonNode jsonNode = mainNode.get(fieldName);
            // if field exists and is an embedded object
            if (jsonNode != null && jsonNode.isObject()) {
                merge(jsonNode, updateNode.get(fieldName));
            } else {
                if (mainNode instanceof ObjectNode) {
                    // Overwrite field
                    JsonNode value = updateNode.get(fieldName);
                    ((ObjectNode) mainNode).set(fieldName, value);
                }
            }

        }
    }

    private void profilesHandle(JsonNode node, List<String> profiles) {
        Map<String, JsonNode> configByProfile = new HashMap<>();
        Iterator<Map.Entry<String, JsonNode>> fields = node.fields();
        while (fields.hasNext()) {
            Map.Entry<String, JsonNode> field = fields.next();
            if (field.getKey().startsWith(PROFILE_KEY)) {
                configByProfile.put(field.getKey().substring(PROFILE_KEY.length()), field.getValue());
            }
        }
        configByProfile.forEach((key, value) -> {
            ((ObjectNode) node).remove(PROFILE_KEY + key);
            if (profiles != null && profiles.contains(key)) {
                merge(node, value);
            }
        });
    }

}
