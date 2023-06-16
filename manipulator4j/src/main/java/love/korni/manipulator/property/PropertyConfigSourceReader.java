package love.korni.manipulator.property;

import love.korni.manipulator.core.gear.file.FileManager;
import love.korni.manipulator.core.gear.file.ResourceFileManager;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;

import java.util.LinkedList;

/**
 * @author Sergei_Kornilov
 */
class PropertyConfigSourceReader {

    private final static String BASE_MANIPULATOR = ResourceFileManager.CLASSPATH + "base-manipulator.yml";
    private final static String MANIPULATOR = ResourceFileManager.CLASSPATH + "manipulator.yml";

    private final FileManager fileManager;
    private final ObjectMapper objectMapper;
    private final LinkedList<PropertyConfigHolder> propertyHolders;

    public PropertyConfigSourceReader(FileManager fileManager) {
        this(fileManager, new ObjectMapper(new YAMLFactory()));
    }

    public PropertyConfigSourceReader(FileManager fileManager, ObjectMapper objectMapper) {
        this.fileManager = fileManager;
        this.objectMapper = objectMapper;
        this.propertyHolders = new LinkedList<>();
        this.objectMapper.setPropertyNamingStrategy(PropertyNamingStrategies.SNAKE_CASE);
        this.objectMapper.findAndRegisterModules();
        read();
    }

    private void read() {
        PropertyConfigHolder baseConfig = new PropertyConfigHolder(fileManager, objectMapper, BASE_MANIPULATOR);
        propertyHolders.addFirst(baseConfig);

        if (fileManager.fileExists(MANIPULATOR)) {
            PropertyConfigHolder defaultConfig = new PropertyConfigHolder(fileManager, objectMapper, MANIPULATOR);
            propertyHolders.addFirst(defaultConfig);
        }
    }

    public boolean contains(String path) {

    }

    public JsonNode getNode(String path) {

    }

}
