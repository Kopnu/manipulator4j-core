package love.korni.manipulator.property;

import love.korni.manipulator.core.gear.file.FileManager;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Sergei_Kornilov
 */
class PropertyConfigSourceReader {

    private final FileManager fileManager;
    private final ObjectMapper objectMapper;

    private List<PropertyConfigHolder> propertyHolders = new ArrayList<>();

    public PropertyConfigSourceReader(FileManager fileManager) {
        this(fileManager, new ObjectMapper(new YAMLFactory()));
    }

    public PropertyConfigSourceReader(FileManager fileManager, ObjectMapper objectMapper) {
        this.fileManager = fileManager;
        this.objectMapper = objectMapper;
        this.objectMapper.setPropertyNamingStrategy(PropertyNamingStrategies.SNAKE_CASE);
        this.objectMapper.findAndRegisterModules();
        read();
    }

    private void read() {
        PropertyConfigHolder baseConfig = new PropertyConfigHolder(fileManager, objectMapper, "base-manipulator.yml");
        PropertyConfigHolder defaultConfig = new PropertyConfigHolder(fileManager, objectMapper, "manipulator.yml");
        propertyHolders.add(baseConfig);
        propertyHolders.add(defaultConfig);
    }


}
