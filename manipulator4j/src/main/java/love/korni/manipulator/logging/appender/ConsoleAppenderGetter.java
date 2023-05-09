/*
 * CivCraft. Do not reproduce without permission in writing.
 * Copyright (c) 2022 GrandProject Team.
 */

package love.korni.manipulator.logging.appender;

import org.apache.logging.log4j.core.config.builder.api.AppenderComponentBuilder;
import org.apache.logging.log4j.core.config.builder.api.ConfigurationBuilder;
import org.apache.logging.log4j.core.config.builder.api.LayoutComponentBuilder;
import org.apache.logging.log4j.core.config.builder.impl.BuiltConfiguration;

/**
 * Создаёт ConsoleAppender.
 *
 * @author Sergei_Konilov
 */
public class ConsoleAppenderGetter extends AppenderGetter {

  public ConsoleAppenderGetter(AppenderGetter next) {
    super(next);
  }

  @Override
  protected boolean canCreate(String type) {
    return "console".equalsIgnoreCase(type);
  }

  @Override
  protected AppenderComponentBuilder createAppender(ConfigurationBuilder<BuiltConfiguration> builder,
                                                    String name) {
    LayoutComponentBuilder layoutBuilder = builder.newLayout("PatternLayout")
        .addAttribute("pattern", "%style{[%d{HH:mm:ss}]}{blue} %highlight{[%t/%level]}{FATAL=red, ERROR=red, WARN=yellow, INFO=green, DEBUG=green, TRACE=blue} %style{(%c{1.})}{cyan} %highlight{%msg%n}{FATAL=red, ERROR=red, WARN=normal, INFO=normal, DEBUG=normal, TRACE=normal}")
        .addAttribute("disableAnsi", System.getProperty("disableAnsi", "false"))
        .addAttribute("alwaysWriteExceptions", "true");
    return builder.newAppender(name, "Console")
        .add(layoutBuilder);
  }
}
