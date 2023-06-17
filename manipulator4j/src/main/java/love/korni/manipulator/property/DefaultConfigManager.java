package love.korni.manipulator.property;

import love.korni.manipulator.core.gear.file.FileManager;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.MissingNode;
import org.apache.commons.lang3.StringUtils;

import java.util.List;

/**
 * @author Sergei_Kornilov
 */
public class DefaultConfigManager implements ConfigManager {

    private final PropertyConfigReader propertyConfigReader;

    public DefaultConfigManager(FileManager fileManager) {
        this(fileManager, List.of());
    }

    public DefaultConfigManager(FileManager fileManager, List<String> propertyProfiles) {
        this.propertyConfigReader = new PropertyConfigReader(fileManager, propertyProfiles);
    }

    /**
     * Ищет в конфигурации необходимую JsonNode по указанному пути.
     *
     * @param path строка вида "path.to.property"
     */
    @Override
    public JsonNode getConfig(String path) {
        if (StringUtils.isBlank(path)) {
            return propertyConfigReader.getPropertyNode();
        }
        return getByPath(propertyConfigReader.getPropertyNode(), path);
    }

    /**
     * Ищем в конфигурации значение по указанному пути.
     *
     * @param path строка вида "path.to.property"
     */
    @Override
    public String getAsText(String path) {
        JsonNode node = getByPath(propertyConfigReader.getPropertyNode(), path);
        if (node instanceof MissingNode) {
            return null;
        }
        return node.asText();
    }

    private JsonNode getByPath(JsonNode node, String path) {
        for (String _path : path.split("\\.")) {
            node = node.get(_path);
        }
        return node != null ? node : MissingNode.getInstance();
    }

}
