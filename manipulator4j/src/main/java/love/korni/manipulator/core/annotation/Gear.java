/*
 * Copyright (c) {year} Sergei Kornilov
 * Licensed under the Apache License, Version 2.0
 */

package love.korni.manipulator.core.annotation;

import static java.lang.annotation.ElementType.METHOD;
import static java.lang.annotation.ElementType.TYPE;

import love.korni.manipulator.core.caldron.metadata.GearType;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Component
 *
 * @author Sergei_Konilov
 */
@Target({TYPE, METHOD})
@Retention(RetentionPolicy.RUNTIME)
public @interface Gear {

    String value() default "";

    GearType scope() default GearType.SINGLETON;

}
