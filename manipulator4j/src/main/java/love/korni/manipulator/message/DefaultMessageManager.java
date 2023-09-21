/*
 * CivCraft. Do not reproduce without permission in writing.
 * Copyright (c) 2022 GrandProject Team.
 */

package love.korni.manipulator.message;

import love.korni.manipulator.message.exception.NoSuchMessageException;
import love.korni.manipulator.message.util.PropertyMultiResourceBundle;

import com.github.benmanes.caffeine.cache.Caffeine;
import com.github.benmanes.caffeine.cache.LoadingCache;
import lombok.Getter;
import lombok.Setter;
import org.apache.commons.lang3.ObjectUtils;

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Locale;
import java.util.MissingResourceException;
import java.util.ResourceBundle;
import java.util.Set;

/**
 * Main localization class. Searches for translations by keys from properties files.<p> Setup:
 * <blockquote><pre>
 * PropertySettings settings = new PropertySettings();
 * settings.setBasenames(new String[]{
 *      "path/to/property",
 *      ...
 * });
 * new DefaultMessageManager(settings);
 * </pre></blockquote><p>
 * <p>
 * Usage example:
 * <blockquote><pre>
 * MessageManager.getMessage("key.from.property", new Object[] {"some", "args", 123});
 * </pre></blockquote><p>
 *
 * @author Sergei_Konilov
 * @see ResourceBundle
 * @see java.util.Properties
 */
public class DefaultMessageManager implements MessageManager {

    protected static final LoadingCache<Locale, ResourceBundle> resourceBundleCache =
            Caffeine.newBuilder().build(DefaultMessageManager::getResourceBundle);
    private static final Set<String> basenameSet = new LinkedHashSet<>();

    @Setter
    @Getter
    private Locale defaultLocale;

    public DefaultMessageManager(PropertySettings settings) {
        defaultLocale = settings.getDefaultLocale();
        if (settings.getBasenames().length > 0) {
            basenameSet.addAll(Arrays.asList(settings.getBasenames()));
        }
    }

    public String getMessage(String code, Object[] args, String defaultMessage,
                             Locale locale) {
        String msg = getMessageInternal(code, args, locale);
        if (msg != null) {
            return msg;
        }
        if (defaultMessage == null) {
            return null;
        }
        return getDefaultMessage(defaultMessage, args, locale);
    }

    public String getMessage(String code) throws NoSuchMessageException {
        return getMessage(code, null);
    }

    public String getMessage(String code, Object[] args) throws NoSuchMessageException {
        return getMessage(code, args, defaultLocale);
    }

    public String getMessage(String code, Object[] args, Locale locale) throws NoSuchMessageException {
        String msg = getMessageInternal(code, args, locale);
        if (msg != null) {
            return msg;
        }
        throw new NoSuchMessageException(code, locale);
    }

    public String getMessage(String code, Object[] args, String defaultMessage) {
        return getMessage(code, args, defaultMessage, defaultLocale);
    }

    private String getMessageInternal(String code, Object[] args, Locale locale) {
        if (code == null) {
            return null;
        }
        if (locale == null) {
            locale = Locale.getDefault();
        }
        ResourceBundle bundle = resourceBundleCache.get(locale);
        String message = resolveMessage(bundle, code);
        if (message != null) {
            args = resolveArguments(args, locale);
            return resolveFormatMessage(message, args);
        }
        return null;
    }

    private String getDefaultMessage(String defaultMessage, Object[] args, Locale locale) {
        if (locale == null) {
            locale = Locale.getDefault();
        }
        ResourceBundle bundle = resourceBundleCache.get(locale);
        String message = resolveMessage(bundle, defaultMessage);
        args = resolveArguments(args, locale);
        if (message != null) {
            return resolveFormatMessage(message, args);
        }
        return resolveFormatMessage(defaultMessage, args);
    }

    private String resolveMessage(ResourceBundle bundle, String code) {
        try {
            return bundle.getString(code);
        } catch (MissingResourceException e) {
            return null;
        }
    }

    private String resolveFormatMessage(String str, Object[] args) {
        return MessageFormat.format(str, args);
    }

    private Object[] resolveArguments(Object[] args, Locale locale) {
        if (ObjectUtils.isEmpty(args)) {
            args = new Object[0];
        }
        List<Object> resolvedArgs = new ArrayList<>(args.length);
        for (Object arg : args) {
            if (arg instanceof Double number) {
                resolvedArgs.add(String.format(locale, "%,.2f", number));
            } else if (arg instanceof Float number) {
                resolvedArgs.add(String.format(locale, "%,.2f", number));
            } else {
                resolvedArgs.add(arg);
            }
        }
        return resolvedArgs.toArray();
    }

    private static ResourceBundle getResourceBundle(Locale locale) {
        return new PropertyMultiResourceBundle(locale, basenameSet);
    }

}
