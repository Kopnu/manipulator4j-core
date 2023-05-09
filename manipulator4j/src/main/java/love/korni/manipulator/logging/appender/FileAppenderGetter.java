/*
 * CivCraft. Do not reproduce without permission in writing.
 * Copyright (c) 2022 GrandProject Team.
 */

package love.korni.manipulator.logging.appender;

import org.apache.logging.log4j.core.config.builder.api.AppenderComponentBuilder;
import org.apache.logging.log4j.core.config.builder.api.ComponentBuilder;
import org.apache.logging.log4j.core.config.builder.api.ConfigurationBuilder;
import org.apache.logging.log4j.core.config.builder.api.LayoutComponentBuilder;
import org.apache.logging.log4j.core.config.builder.impl.BuiltConfiguration;

/**
 * FileAppenderGetter
 *
 * @author Sergei_Konilov
 */
public class FileAppenderGetter extends AppenderGetter {

  public FileAppenderGetter(AppenderGetter next) {
    super(next);
  }

  @Override
  protected boolean canCreate(String type) {
    return "file".equalsIgnoreCase(type);
  }

  @Override
  protected AppenderComponentBuilder createAppender(ConfigurationBuilder<BuiltConfiguration> builder,
                                                    String name) {
    LayoutComponentBuilder layoutBuilder = builder.newLayout("PatternLayout")
        .addAttribute("pattern", "[%d{HH:mm:ss}] [%t/%level] (%c{1.}) %msg%n");
    ComponentBuilder<?> triggeringPolicy = builder.newComponent("Policies")
        .addComponent(builder.newComponent("TimeBasedTriggeringPolicy")
            .addAttribute("interval", "1")
            .addAttribute("modulate", "true"))
        .addComponent(builder.newComponent("SizeBasedTriggeringPolicy")
            .addAttribute("size", "20 MB"))
        .addComponent(builder.newComponent("OnStartupTriggeringPolicy"));
    return builder.newAppender(name, "RollingRandomAccessFile")
        .addAttribute("fileName", "logs/" + name.toLowerCase() + ".log")
        .addAttribute("filePattern", "logs/%d{yyyy-MM-dd}-" + name.toLowerCase() + "-%i.log.gz")
        .add(layoutBuilder)
        .addComponent(triggeringPolicy);
  }
}
