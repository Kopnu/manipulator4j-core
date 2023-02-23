/*
 * Copyright (c) {year} Sergei Kornilov
 * Licensed under the Apache License, Version 2.0
 */

package love.korni.manipulator.core.annotation;

import static java.lang.annotation.ElementType.CONSTRUCTOR;
import static java.lang.annotation.ElementType.FIELD;
import static java.lang.annotation.ElementType.METHOD;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Autoinject
 *
 * @author Sergei_Konilov
 */
@Target({FIELD, CONSTRUCTOR, METHOD})
@Retention(RetentionPolicy.RUNTIME)
public @interface Autoinject {
}
