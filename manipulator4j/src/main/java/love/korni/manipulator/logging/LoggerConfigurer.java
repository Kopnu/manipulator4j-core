/*
 * CivCraft. Do not reproduce without permission in writing.
 * Copyright (c) 2022 GrandProject Team.
 */

package love.korni.manipulator.logging;

import love.korni.manipulator.core.annotation.Autoinject;
import love.korni.manipulator.core.gear.file.FileManager;
import love.korni.manipulator.core.gear.file.ResourceFileManager;
import love.korni.manipulator.logging.appender.AppenderGetter;
import love.korni.manipulator.logging.appender.ConsoleAppenderGetter;
import love.korni.manipulator.logging.appender.FileAppenderGetter;
import love.korni.manipulator.logging.appender.QueueAppenderGetter;
import love.korni.manipulator.logging.exception.LoggerConfigurationException;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;
import lombok.Data;
import lombok.experimental.Accessors;
import lombok.extern.slf4j.Slf4j;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.core.LoggerContext;
import org.apache.logging.log4j.core.config.builder.api.AppenderComponentBuilder;
import org.apache.logging.log4j.core.config.builder.api.ConfigurationBuilder;
import org.apache.logging.log4j.core.config.builder.api.ConfigurationBuilderFactory;
import org.apache.logging.log4j.core.config.builder.api.LoggerComponentBuilder;
import org.apache.logging.log4j.core.config.builder.api.RootLoggerComponentBuilder;
import org.apache.logging.log4j.core.config.builder.impl.BuiltConfiguration;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.function.Supplier;

/**
 * Конфигуратор логгирования.
 *
 * @author Sergei_Konilov
 */
@Slf4j
public class LoggerConfigurer {

    private final FileManager fileManager;

    private final ObjectMapper objectMapper;
    private final AppenderGetter appenderGetter;

    @Autoinject
    public LoggerConfigurer(FileManager fileManager) {
        this(fileManager, new ObjectMapper(new YAMLFactory()));
    }

    public LoggerConfigurer(FileManager fileManager, ObjectMapper objectMapper) {
        this.fileManager = fileManager;
        this.objectMapper = objectMapper;
        this.objectMapper.setPropertyNamingStrategy(PropertyNamingStrategies.SNAKE_CASE);
        this.objectMapper.findAndRegisterModules();

        appenderGetter = new ConsoleAppenderGetter(new QueueAppenderGetter(new FileAppenderGetter(null)));
    }

    public void configure(String pathToConfigFile) {
        LoggingConfig loggingConfig = getConfig(pathToConfigFile);

        if (loggingConfig == null) {
            return;
        }

        ConfigurationBuilder<BuiltConfiguration> builder = ConfigurationBuilderFactory.newConfigurationBuilder();

        buildAppenders(builder, loggingConfig).forEach(builder::add);
        builder.add(buildRootLogger(builder, loggingConfig));
        buildLoggers(builder, loggingConfig).forEach((packageName, logger) -> builder.add(logger));

        final LoggerContext ctx = (LoggerContext) LogManager.getContext(false);
        ctx.setConfiguration(builder.build());
        log.info("Logging setup completed!");
    }

    private List<AppenderComponentBuilder> buildAppenders(ConfigurationBuilder<BuiltConfiguration> builder,
                                                          LoggingConfig loggingConfig) {
        List<AppenderComponentBuilder> appenders = loggingConfig.getAppenderConfigs().stream()
                .map(appenderConfig -> {
                    String name = appenderConfig.getName();
                    String type = appenderConfig.getType();
                    return appenderGetter.getAppender(builder, name, type);
                })
                .toList();
        return appenders;
    }

    private RootLoggerComponentBuilder buildRootLogger(ConfigurationBuilder<BuiltConfiguration> builder,
                                                       LoggingConfig loggingConfig) {
        RootLoggerComponentBuilder rootLoggerComponent = null;
        for (LoggingConfig.AppenderConfig config : loggingConfig.getAppenderConfigs()) {
            for (Map.Entry<String, String> entry : config.getPackages().entrySet()) {
                if (entry.getKey().equalsIgnoreCase("root")) {
                    if (rootLoggerComponent == null) {
                        rootLoggerComponent = builder.newRootLogger(Level.ALL);
                    }
                    rootLoggerComponent.add(builder.newAppenderRef(config.getName())
                            .addAttribute("level", entry.getValue()));
                }
            }
        }
        return rootLoggerComponent;
    }

    private Map<String, LoggerComponentBuilder> buildLoggers(ConfigurationBuilder<BuiltConfiguration> builder,
                                                             LoggingConfig loggingConfig) {
        Map<String, LoggerComponentBuilder> loggers = new HashMap<>();
        loggingConfig.getAppenderConfigs().forEach(appenderConfig -> {
            appenderConfig.getPackages().forEach((packageName, level) -> {
                if (!packageName.equalsIgnoreCase("root")) {
                    LoggerComponentBuilder defaultValue = builder.newLogger(packageName)
                            .addAttribute("additivity", "false");
                    LoggerComponentBuilder logger = loggers.getOrDefault(packageName, defaultValue);
                    logger.add(builder.newAppenderRef(appenderConfig.getName())
                            .addAttribute("level", level));
                    loggers.put(packageName, logger);
                }
            });
        });
        return loggers;
    }

    private LoggingConfig getConfig(String pathToConfigFile) {
        try {
            JsonNode logging = readConfigFile(pathToConfigFile).get("logging");

            List<LoggingConfig.AppenderConfig> appenders = new ArrayList<>();
            LoggingConfig.AppenderConfig consoleAppender = new LoggingConfig.AppenderConfig()
                    .setName(getNodeValue(() -> logging.get("name").asText("SysOut"), "SysOut"))
                    .setType(getNodeValue(() -> logging.get("type").asText("CONSOLE").toUpperCase(), "CONSOLE"))
                    .setPackages(getLevels(logging.get("level")));
            appenders.add(consoleAppender);
            JsonNode additionalAppendersNode = logging.get("additional");
            for (Iterator<JsonNode> it = additionalAppendersNode.elements(); it.hasNext(); ) {
                JsonNode jsonNode = it.next();
                String type = jsonNode.get("type").asText().toUpperCase();
                if (type.equalsIgnoreCase("console")) {
                    throw new LoggerConfigurationException("Console appender is not configured in \"additional\" block");
                }
                LoggingConfig.AppenderConfig additionalAppenderConfig = new LoggingConfig.AppenderConfig()
                        .setName(getNodeValue(() -> jsonNode.get("name").asText(), "Appender" + appenders.size()))
                        .setType(type)
                        .setPackages(getLevels(jsonNode.get("level")));
                appenders.add(additionalAppenderConfig);
            }

            return new LoggingConfig().setAppenderConfigs(appenders);
        } catch (RuntimeException e) {
            log.error("Error while logging configuration: ", e);
            return null;
        }
    }

    private <T> T getNodeValue(Supplier<T> supplier, T defaultValue) {
        try {
            return supplier.get();
        } catch (NullPointerException e) {
            return defaultValue;
        }
    }

    private Map<String, String> getLevels(JsonNode levelNode) {
        Map<String, String> packages = new HashMap<>();
        for (Iterator<Map.Entry<String, JsonNode>> it = levelNode.fields(); it.hasNext(); ) {
            Map.Entry<String, JsonNode> entry = it.next();
            packages.put(entry.getKey(), entry.getValue().asText().toUpperCase());
        }
        return packages;
    }

    private JsonNode readConfigFile(String path) {
        try {
            InputStream inputStream = fileManager.readFile(ResourceFileManager.CLASSPATH + path);
            return objectMapper.readTree(inputStream);
        } catch (IOException e) {
            throw new LoggerConfigurationException(e);
        }
    }

    @Data
    @Accessors(chain = true)
    private static class LoggingConfig {

        private List<AppenderConfig> appenderConfigs;

        @Data
        @Accessors(chain = true)
        public static class AppenderConfig {
            private String name;
            private String type;
            private Map<String, String> packages;
        }

    }

}
