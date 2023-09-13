/*
 * Copyright (c) {year} Sergei Kornilov
 * Licensed under the Apache License, Version 2.0
 */

package love.korni.manipulator.core.caldron.metadata;

import static love.korni.manipulator.core.caldron.GearFactoryUtils.getGearAnnotationValue;

import love.korni.manipulator.core.annotation.AfterConstruct;
import love.korni.manipulator.core.annotation.Gear;
import love.korni.manipulator.core.exception.GearConstructionException;
import love.korni.manipulator.util.ReflectionUtils;

import lombok.Getter;

import java.lang.reflect.Method;
import java.util.List;

/**
 * Реализация {@link ArrayGearMetadata}, хранящая в себе метаинформацию шестерни, построенной, когда аннотация {@link Gear} на классе.
 *
 * @author Sergei_Konilov
 */
public class ClassGearMetadata extends AbstractGearMetadata {

    @Getter
    protected final List<Class<?>> interfaces;
    @Getter
    protected final Method afterConstructMethod;

    public ClassGearMetadata(Class<?> clazz) {
        this(clazz, getGearAnnotationValue(clazz.getAnnotation(Gear.class), ""), null);
    }

    public ClassGearMetadata(Class<?> clazz, String name, GearMetadata parent) {
        super(clazz.getCanonicalName(), name, clazz);
        this.parent = parent;
        this.interfaces = ReflectionUtils.getInterfaces(clazz);
        this.afterConstructMethod = findAfterConstructMethod(clazz);
    }

    private Method findAfterConstructMethod(Class<?> clazz) {
        List<Method> methodsAnnotated = ReflectionUtils.findMethodsAnnotated(clazz, AfterConstruct.class);
        if (methodsAnnotated.isEmpty()) {
            return null;
        }
        if (methodsAnnotated.size() > 1) {
            throw new GearConstructionException("Found %d \"@AfterConstruct\" methods. Expected one.".formatted(methodsAnnotated.size()));
        }
        return methodsAnnotated.get(0);
    }
}
