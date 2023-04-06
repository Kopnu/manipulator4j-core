/*
 * CivCraft. Do not reproduce without permission in writing.
 * Copyright (c) 2022 GrandProject Team.
 */

package love.korni.manipulator.localization.util;

import love.korni.manipulator.util.Assert;

import java.util.Collection;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.ResourceBundle;
import java.util.Set;

/**
 * Property ResourceBundle with multifile loading support.
 *
 * @author Sergei_Konilov
 * @see java.util.PropertyResourceBundle
 */
public class PropertyMultiResourceBundle extends ResourceBundle {

  private final Map<String, Object> lookup;

  public PropertyMultiResourceBundle(Locale locale, Collection<String> basenames) {
    lookup = new HashMap<>();
    for (String basename : basenames) {
      ResourceBundle bundle = ResourceBundle.getBundle(basename, locale, new Control() {
        @Override
        public Locale getFallbackLocale(String baseName, Locale locale) {
          return Locale.ROOT;
        }
      });
      Enumeration<String> keys = bundle.getKeys();
      while (keys.hasMoreElements()) {
        String key = keys.nextElement();
        lookup.put(key, bundle.getString(key));
      }
    }
  }

  @Override
  public Object handleGetObject(String key) {
    Assert.notNull(key, "'key' must not be null");
    return lookup.get(key);
  }

  @Override
  public Enumeration<String> getKeys() {
    ResourceBundle parent = this.parent;
    return new ResourceBundleEnumeration(lookup.keySet(), (parent != null) ? parent.getKeys() : null);
  }

  protected Set<String> handleKeySet() {
    return lookup.keySet();
  }
}
