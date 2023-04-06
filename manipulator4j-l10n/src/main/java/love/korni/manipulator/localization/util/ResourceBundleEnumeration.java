/*
 * CivCraft. Do not reproduce without permission in writing.
 * Copyright (c) 2022 GrandProject Team.
 */

package love.korni.manipulator.localization.util;

import java.util.Enumeration;
import java.util.Iterator;
import java.util.NoSuchElementException;
import java.util.Set;

/**
 * Copy of <b>sun.util.ResourceBundleEnumeration</b>. Bcz it's easier than adding some parameters to the Java module
 * system. :/ You can see class from <i>sun</i> package, but can't import.
 */
public class ResourceBundleEnumeration implements Enumeration<String> {

  private Set<String> set;
  private Iterator<String> iterator;
  private Enumeration<String> enumeration; // may remain null

  /**
   * Constructs a resource bundle enumeration.
   *
   * @param set         an set providing some elements of the enumeration
   * @param enumeration an enumeration providing more elements of the enumeration. enumeration may be null.
   */
  public ResourceBundleEnumeration(Set<String> set, Enumeration<String> enumeration) {
    this.set = set;
    this.iterator = set.iterator();
    this.enumeration = enumeration;
  }

  private String next = null;

  public boolean hasMoreElements() {
    if (next == null) {
      if (iterator.hasNext()) {
        next = iterator.next();
      } else if (enumeration != null) {
        while (next == null && enumeration.hasMoreElements()) {
          next = enumeration.nextElement();
          if (set.contains(next)) {
            next = null;
          }
        }
      }
    }
    return next != null;
  }

  public String nextElement() {
    if (hasMoreElements()) {
      String result = next;
      next = null;
      return result;
    } else {
      throw new NoSuchElementException();
    }
  }
}
