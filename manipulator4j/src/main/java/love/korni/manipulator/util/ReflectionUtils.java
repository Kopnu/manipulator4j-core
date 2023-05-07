/*
 * Copyright (c) {year} Sergei Kornilov
 * Licensed under the Apache License, Version 2.0
 */

package love.korni.manipulator.util;

import lombok.experimental.UtilityClass;

import java.lang.annotation.Annotation;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * ReflectionUtils
 *
 * @author Sergei_Konilov
 */
@UtilityClass
public class ReflectionUtils {

    public void makeAccessible(Field field) {
        if (!Modifier.isPublic(field.getModifiers()) || !Modifier.isPublic(field.getDeclaringClass().getModifiers()) || Modifier.isFinal(field.getModifiers())) {
            field.setAccessible(true);
        }
    }
    public void makeAccessible(Constructor<?> constructor) {
        if (!Modifier.isPublic(constructor.getModifiers()) || !Modifier.isPublic(constructor.getDeclaringClass().getModifiers()) || Modifier.isFinal(constructor.getModifiers())) {
            constructor.setAccessible(true);
        }
    }

    public List<Field> findFieldsAnnotated(Class<?> clazz, Class<? extends Annotation> annotation) {
        return getDeclaredFieldsWithParents(clazz).stream()
            .filter(method -> method.isAnnotationPresent(annotation))
            .toList();
    }

    private Set<Field> getDeclaredFieldsWithParents(Class<?> clazz) {
        Set<Field> fields = new HashSet<>(List.of(clazz.getDeclaredFields()));

        Class<?> superclass = clazz.getSuperclass();
        if (superclass != null && !superclass.equals(Object.class)) {
            fields.addAll(getDeclaredFieldsWithParents(superclass));
        }
        return fields;
    }

    public List<Method> findMethodsAnnotated(Class<?> clazz, Class<? extends Annotation> annotation) {
        return Arrays.stream(clazz.getDeclaredMethods())
            .filter(method -> method.isAnnotationPresent(annotation))
            .toList();
    }

    public List<Annotation> getTypeAnnotations(Class<?> clazz) {
        return Arrays.stream(clazz.getDeclaredAnnotations()).toList();
    }

    public List<Constructor<?>> findConstructorsAnnotated(Class<?> clazz, Class<? extends Annotation> annotation) {
        return Arrays.stream(clazz.getDeclaredConstructors())
            .filter(method -> method.isAnnotationPresent(annotation))
            .toList();
    }

    public List<Class<?>> getInterfaces(Class<?> clazz) {
        return Arrays.stream(clazz.getInterfaces()).toList();
    }

    public Class<?>[] getClasses(Object... objects) {
        Class<?>[] classes = new Class<?>[objects.length];
        for (int i = 0; i < objects.length; i++) {
            classes[i] = objects[i].getClass();
        }
        return classes;
    }
}
