/*
 * Copyright (c) {year} Sergei Kornilov
 * Licensed under the Apache License, Version 2.0
 */

package love.korni.manipulator.core.caldron.metadata;

import love.korni.manipulator.core.annotation.Gear;

import org.apache.commons.lang3.StringUtils;

import java.lang.reflect.Method;

/**
 * Реализация {@link ArrayGearMetadata}, хранящая в себе метаинформацию шестерни, построенной, когда аннотация {@link Gear} на методе.
 *
 * @author Sergei_Konilov
 */
public class MethodGearMetadata extends AbstractGearMetadata {

    private final Method method;

    public MethodGearMetadata(Method method) {
        super(name(method), method.getReturnType());
        this.method = method;
        this.parent = new ClassGearMetadata(method.getDeclaringClass());
    }

    public Method getMethod() {
        return method;
    }

    private static String name(Method method) {
        String gearValue = method.getAnnotation(Gear.class).value();
        if (StringUtils.isBlank(gearValue)) {
            gearValue = method.getName();
        }
        return gearValue;
    }
}
