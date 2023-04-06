/*
 * Copyright (c) {year} Sergei Kornilov
 * Licensed under the Apache License, Version 2.0
 */

package love.korni.manipulator.core.caldron.metadata;

import static love.korni.manipulator.core.caldron.GearFactoryUtils.getGearAnnotationValue;

import love.korni.manipulator.core.annotation.Gear;
import love.korni.manipulator.util.ReflectionUtils;

import lombok.Getter;

import java.util.List;

/**
 * ClassComponentMetadata
 *
 * @author Sergei_Konilov
 */
public class ClassGearMetadata extends AbstractGearMetadata {

    @Getter
    protected final List<Class<?>> interfaces;

    public ClassGearMetadata(Class<?> clazz) {
        this(clazz, getGearAnnotationValue(clazz.getAnnotation(Gear.class), ""), null);
    }

    public ClassGearMetadata(Class<?> clazz, String name, GearMetadata parent) {
        super(clazz.getCanonicalName(), name, clazz);
        this.parent = parent;
        this.interfaces = ReflectionUtils.getInterfaces(clazz);
    }

    @Override
    public Boolean isMethod() {
        return false;
    }

}
