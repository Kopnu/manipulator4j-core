package love.korni.manipulator.property;

import com.fasterxml.jackson.databind.JsonNode;

/**
 * @author Sergei_Kornilov
 */
public interface ConfigManager {

    JsonNode getConfig(String path);

    String getAsText(String path);
}
