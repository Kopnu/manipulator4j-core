/*
 * CivCraft. Do not reproduce without permission in writing.
 * Copyright (c) 2022 GrandProject Team.
 */

package love.korni.manipulator.logging.appender;

import org.apache.logging.log4j.core.config.builder.api.AppenderComponentBuilder;
import org.apache.logging.log4j.core.config.builder.api.ConfigurationBuilder;
import org.apache.logging.log4j.core.config.builder.impl.BuiltConfiguration;

/**
 * Chain of Responsibility pattern для создания аппендеров.
 *
 * @author Sergei_Konilov
 */
public abstract class AppenderGetter {

  private AppenderGetter next;

  public AppenderGetter(AppenderGetter next) {
    this.next = next;
  }

  public AppenderComponentBuilder getAppender(ConfigurationBuilder<BuiltConfiguration> builder,
                              String name,
                              String type) {
    if (canCreate(type)) {
      return createAppender(builder, name);
    }
    if (next != null) {
      return next.getAppender(builder, name, type);
    }
    return null;
  }

  protected abstract boolean canCreate(String type);

  protected abstract AppenderComponentBuilder createAppender(ConfigurationBuilder<BuiltConfiguration> builder,
                                                             String name);
}
