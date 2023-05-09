/*
 * CivCraft. Do not reproduce without permission in writing.
 * Copyright (c) 2022 GrandProject Team.
 */

package love.korni.manipulator.logging.exception;

/**
 * LoggerConfigurationException
 *
 * @author Sergei_Konilov
 */
public class LoggerConfigurationException extends RuntimeException {

  public LoggerConfigurationException() {
  }

  public LoggerConfigurationException(String message) {
    super(message);
  }

  public LoggerConfigurationException(Throwable cause) {
    super(cause);
  }
}
