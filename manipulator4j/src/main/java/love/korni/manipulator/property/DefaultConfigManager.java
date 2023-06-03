package love.korni.manipulator.property;

import love.korni.manipulator.core.gear.file.FileManager;

import com.fasterxml.jackson.databind.JsonNode;

import java.util.HashMap;
import java.util.Map;

/**
 * @author Sergei_Kornilov
 */
public class DefaultConfigManager implements ConfigManager {

    private final Map<String, JsonNode> configByProfile = new HashMap<>();

    private PropertyConfigSourceReader propertyConfigSourceReader;

    public DefaultConfigManager(FileManager fileManager) {
        this.propertyConfigSourceReader = new PropertyConfigSourceReader(fileManager);
    }

    /**
     * Ищет в конфигурации необходимую JsonNode по указанному пути.
     *
     * @param path строка вида "path.to.property"
     */
    @Override
    public JsonNode getConfig(String path) {

    }

    @Override
    public <T> T getValue(String path, Class<T> type) {

    }

}
