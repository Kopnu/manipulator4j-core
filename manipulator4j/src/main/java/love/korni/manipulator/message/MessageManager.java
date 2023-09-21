/*
 * CivCraft. Do not reproduce without permission in writing.
 * Copyright (c) 2022 GrandProject Team.
 */

package love.korni.manipulator.message;

import love.korni.manipulator.message.exception.NoSuchMessageException;


import java.util.Locale;

/**
 * The main interface for working with localization.
 *
 * @see DefaultMessageManager
 * @author Sergei_Konilov
 */
public interface MessageManager {

  String getMessage(String code) throws NoSuchMessageException;

  String getMessage(String code, Object... args) throws NoSuchMessageException;

  String getMessage(String code, Object[] args, Locale locale) throws NoSuchMessageException;

  String getMessage(String code, Object[] args, String defaultMessage);

  String getMessage(String code, Object[] args, String defaultMessage, Locale locale);

}
