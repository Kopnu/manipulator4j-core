/*
 * Copyright (c) {year} Sergei Kornilov
 * Licensed under the Apache License, Version 2.0
 */

package love.korni.manipulator.core.caldron.metadata;

import love.korni.manipulator.core.annotation.Autoinject;
import love.korni.manipulator.util.ReflectionUtils;

import lombok.Getter;
import org.apache.commons.lang3.StringUtils;

import java.lang.annotation.Annotation;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.List;
import java.util.Locale;
import java.util.Set;

/**
 * AbstractGearMetadata
 *
 * @author Sergei_Konilov
 */
@Getter
public abstract class AbstractGearMetadata implements GearMetadata {

    protected final String canonicalName;
    protected final String name;
    protected final GearType type = GearType.SINGLETON;
    protected final Class<?> gearClass;

    protected GearMetadata parent;
    protected List<Annotation> typeAnnotations;
    protected List<Field> fieldsAnnotated;
    protected List<Method> methodsAnnotated;
    protected List<Constructor<?>> constructorsAnnotated;

    public AbstractGearMetadata(Class<?> gearClass) {
        this(gearClass.getCanonicalName(), gearClass.getSimpleName(), gearClass);
    }

    public AbstractGearMetadata(String name, Class<?> gearClass) {
        this(gearClass.getCanonicalName(), name, gearClass);
    }

    public AbstractGearMetadata(String canonicalName, String name, Class<?> gearClass) {
        this.gearClass = gearClass;
        this.canonicalName = StringUtils.isEmpty(canonicalName) ? gearClass.getCanonicalName() : canonicalName;
        this.name = StringUtils.isEmpty(name) ? gearClass.getSimpleName() : name;
        analyzeGear();
    }

    private void analyzeGear() {
        typeAnnotations = ReflectionUtils.getTypeAnnotations(gearClass);
        fieldsAnnotated = ReflectionUtils.findFieldsAnnotated(gearClass, Autoinject.class);
        methodsAnnotated = ReflectionUtils.findMethodsAnnotated(gearClass, Autoinject.class);
        constructorsAnnotated = ReflectionUtils.findConstructorsAnnotated(gearClass, Autoinject.class);
    }

    public String getGearName() {
        String name;
        if (!StringUtils.isBlank(this.name) ) {
            name = this.name;
        } else if (!StringUtils.isBlank(this.canonicalName)) {
            name = this.canonicalName;
        } else {
            name = this.gearClass.getName();
        }
        return name.toLowerCase();
    }

    @Override
    public String toString() {
        return "AbstractGearMetadata{" +
                "gearName='" + getGearName() + '\'' +
                ", canonicalName='" + canonicalName + '\'' +
                ", name='" + name + '\'' +
                ", type=" + type +
                ", gearClass=" + gearClass +
                ", parent=" + parent +
                '}';
    }
}
