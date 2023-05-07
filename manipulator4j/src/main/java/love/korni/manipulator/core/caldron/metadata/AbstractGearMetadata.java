/*
 * Copyright (c) {year} Sergei Kornilov
 * Licensed under the Apache License, Version 2.0
 */

package love.korni.manipulator.core.caldron.metadata;

import love.korni.manipulator.core.annotation.Autoinject;
import love.korni.manipulator.core.annotation.Gear;
import love.korni.manipulator.core.caldron.GearMetadataFactory;
import love.korni.manipulator.util.ReflectionUtils;

import lombok.Getter;
import org.apache.commons.lang3.StringUtils;

import java.lang.annotation.Annotation;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.List;
import java.util.Optional;
import java.util.function.Supplier;

/**
 * Абстрактный класс метаинформации для шестерни реализующий интерфейс {@link GearMetadata}.
 * Содержит в себе общие компоненты необходимые для всех последующих реализаций.
 * Анализирует
 *
 * @author Sergei_Konilov
 */
@Getter
public abstract non-sealed class AbstractGearMetadata implements GearMetadata {

    protected final String canonicalName;
    protected final String name;
    protected final GearType type;
    protected final Class<?> gearClass;

    protected GearMetadata parent;
    protected List<Annotation> typeAnnotations;
    protected List<Field> fieldsAnnotated;
    protected List<Method> methodsAnnotated;
    protected List<Constructor<?>> constructorsAnnotated;

    protected GearMetadataFactory factory;

    public AbstractGearMetadata(Class<?> gearClass) {
        this(gearClass.getCanonicalName(), gearClass.getSimpleName(), gearClass);
    }

    public AbstractGearMetadata(String name, Class<?> gearClass) {
        this(gearClass.getCanonicalName(), name, gearClass);
    }

    public AbstractGearMetadata(String canonicalName, String name, Class<?> gearClass) {
        this.gearClass = gearClass;
        this.canonicalName = StringUtils.isEmpty(canonicalName) ? gearClass.getCanonicalName() : canonicalName;
        this.name = StringUtils.isBlank(name) ? gearClass.getSimpleName() : name;
        analyzeGear();
        Optional<Gear> gearAnnotation = typeAnnotations.stream().filter(annotation -> annotation instanceof Gear).map(annotation -> (Gear) annotation).findFirst();
        this.type = gearAnnotation.isPresent() ? gearAnnotation.get().scope() : GearType.SINGLETON;
    }

    private void analyzeGear() {
        typeAnnotations = ReflectionUtils.getTypeAnnotations(gearClass);
        fieldsAnnotated = ReflectionUtils.findFieldsAnnotated(gearClass, Autoinject.class);
        methodsAnnotated = ReflectionUtils.findMethodsAnnotated(gearClass, Autoinject.class);
        constructorsAnnotated = ReflectionUtils.findConstructorsAnnotated(gearClass, Autoinject.class);
    }

    public String getGearName() {
        String name;
        if (!StringUtils.isBlank(this.name)) {
            name = this.name;
        } else if (!StringUtils.isBlank(this.canonicalName)) {
            name = this.canonicalName;
        } else {
            name = this.gearClass.getName();
        }
        return name.toLowerCase();
    }

    protected GearMetadataFactory getFactory(Supplier<GearMetadataFactory> supplier) {
        return factory != null ? factory : supplier.get();
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
