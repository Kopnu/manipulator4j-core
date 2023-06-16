package love.korni.manipulator.property;

import love.korni.manipulator.core.gear.file.FileManager;

import com.fasterxml.jackson.databind.JsonNode;

/**
 * @author Sergei_Kornilov
 */
public class DefaultConfigManager implements ConfigManager {

    private final PropertyConfigSourceReader propertyConfigSourceReader;

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
        boolean isContains = propertyConfigSourceReader.contains(path);
        if (isContains) {
            return propertyConfigSourceReader.getNode(path);
        }
        return null;
    }

    /**
     * Ищем в конфигурации значение по указанному пути.
     *
     * @param path строка вида "path.to.property"
     */
    @Override
    public <T> T getValue(String path, Class<T> type) {
        boolean isContains = propertyConfigSourceReader.contains(path);
        if (isContains) {
            JsonNode node = propertyConfigSourceReader.getNode(path);
            if (node.isValueNode()) {
                return ;
            }
        }
        return null;
    }

}
