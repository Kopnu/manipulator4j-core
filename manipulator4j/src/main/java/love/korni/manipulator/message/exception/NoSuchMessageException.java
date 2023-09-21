/*
 * CivCraft. Do not reproduce without permission in writing.
 * Copyright (c) 2022 GrandProject Team.
 */

package love.korni.manipulator.message.exception;

import java.util.Locale;

/**
 * @author Sergei_Konilov
 */
public class NoSuchMessageException extends RuntimeException {

  public NoSuchMessageException(String code, Locale locale) {
    super("No message found under code '" + code + "' for locale '" + locale + "'.");
  }

  public NoSuchMessageException(String code) {
    super("No message found under code '" + code + "' for locale '" + Locale.getDefault() + "'.");
  }

}
