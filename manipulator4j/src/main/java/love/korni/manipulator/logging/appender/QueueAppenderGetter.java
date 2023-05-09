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
 * QueueAppenderGetter
 *
 * @author Sergei_Konilov
 */
public class QueueAppenderGetter extends AppenderGetter {

  public QueueAppenderGetter(AppenderGetter next) {
    super(next);
  }

  @Override
  protected boolean canCreate(String type) {
    return "queue".equalsIgnoreCase(type);
  }

  @Override
  protected AppenderComponentBuilder createAppender(ConfigurationBuilder<BuiltConfiguration> builder,
                                                    String name) {
    LayoutComponentBuilder layoutBuilder = builder.newLayout("PatternLayout")
        .addAttribute("pattern", "[%d{HH:mm:ss} %level]: %msg{nolookups}%n");
    return builder.newAppender(name, "Queue")
        .addAttribute("ignoreExceptions", "true")
        .add(layoutBuilder);
  }
}
