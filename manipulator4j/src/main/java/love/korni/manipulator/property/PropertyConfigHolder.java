package love.korni.manipulator.property;

import love.korni.manipulator.core.gear.file.FileManager;
import love.korni.manipulator.core.gear.file.ResourceFileManager;
import love.korni.manipulator.logging.exception.LoggerConfigurationException;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.io.InputStream;

/**
 * @author Sergei_Kornilov
 */
class PropertyConfigHolder {

    private final FileManager fileManager;
    private final ObjectMapper objectMapper;

    private final String path;
    private final JsonNode propertyNode;

    public PropertyConfigHolder(FileManager fileManager, ObjectMapper objectMapper, String path) {
        this.fileManager = fileManager;
        this.objectMapper = objectMapper;
        this.path = path;
        this.propertyNode = readConfigFile(path);
    }

    public String getPath() {
        return path;
    }

    public JsonNode getPropertyNode() {
        return propertyNode;
    }

    private JsonNode readConfigFile(String path) {
        try {
            InputStream inputStream = fileManager.readFile(path);
            return objectMapper.readTree(inputStream);
        } catch (IOException e) {
            throw new LoggerConfigurationException(e);
        }
    }
}
