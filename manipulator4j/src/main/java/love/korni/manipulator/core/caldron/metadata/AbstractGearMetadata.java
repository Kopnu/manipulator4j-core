/*
 * Copyright (c) {year} Sergei Kornilov
 * Licensed under the Apache License, Version 2.0
 */

package love.korni.manipulator.core.caldron.metadata;

import love.korni.manipulator.core.annotation.Autoinject;
import love.korni.manipulator.core.annotation.Gear;
import love.korni.manipulator.util.ReflectionUtils;

import lombok.Getter;
import org.apache.commons.lang3.StringUtils;

import java.lang.annotation.Annotation;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.List;
import java.util.Optional;

/**
 * Абстрактный класс метаинформации для шестерни реализующий интерфейс {@link GearMetadata}.
 * Содержит в себе общие компоненты необходимые для всех последующих реализаций.
 * Анализирует
 *
 * @author Sergei_Konilov
 */
@Getter
public abstract non-sealed class AbstractGearMetadata implements GearMetadata {

    protected final Class<?> gearClass;
    protected final String canonicalName;
    protected final String name;
    protected final GearScope scope;
    protected final String[] profiles;
    protected final boolean primary;

    protected GearMetadata parent;
    protected List<Annotation> typeAnnotations;
    protected List<Field> fieldsAutoinject;
    protected List<Method> methodsAutoinject;
    protected List<Constructor<?>> constructorsAutoinject;

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
        Optional<Gear> gearAnnotation = getTypeAnnotation(Gear.class);
        this.scope = gearAnnotation.map(Gear::scope).orElse(GearScope.SINGLETON);
        this.profiles = gearAnnotation.map(Gear::profiles).orElse(new String[]{"default"});
        this.primary = gearAnnotation.map(Gear::isPrimary).orElse(Boolean.FALSE);
    }

    private void analyzeGear() {
        typeAnnotations = ReflectionUtils.getTypeAnnotations(gearClass);
        fieldsAutoinject = ReflectionUtils.findFieldsAnnotated(gearClass, Autoinject.class);
        methodsAutoinject = ReflectionUtils.findMethodsAnnotated(gearClass, Autoinject.class);
        constructorsAutoinject = ReflectionUtils.findConstructorsAnnotated(gearClass, Autoinject.class);
    }

    public String getGearName() {
        String name = this.canonicalName + "@" + this.name;
        return name.toLowerCase();
    }

    @SuppressWarnings("unchecked")
    protected <T> Optional<T> getTypeAnnotation(Class<T> annotationClass) {
        return typeAnnotations.stream()
                .filter(annotation -> annotationClass.isAssignableFrom(annotation.annotationType()))
                .map(annotation -> (T) annotation)
                .findFirst();
    }

    @Override
    public String toString() {
        return "AbstractGearMetadata{" +
               "gearName='" + getGearName() + '\'' +
               ", canonicalName='" + canonicalName + '\'' +
               ", name='" + name + '\'' +
               ", type=" + scope +
               ", gearClass=" + gearClass +
               ", parent=" + parent +
               '}';
    }
}
