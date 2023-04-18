/*
 * Copyright (c) {year} Sergei Kornilov
 * Licensed under the Apache License, Version 2.0
 */

package love.korni.manipulator.util;

import java.lang.annotation.Annotation;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.Arrays;
import java.util.List;

/**
 * ReflectionUtils
 *
 * @author Sergei_Konilov
 */
public class ReflectionUtils {

    public static void makeAccessible(Field field) {
        if (!Modifier.isPublic(field.getModifiers()) || !Modifier.isPublic(field.getDeclaringClass().getModifiers()) || Modifier.isFinal(field.getModifiers())) {
            field.setAccessible(true);
        }
    }

    public static List<Field> findFieldsAnnotated(Class<?> clazz, Class<? extends Annotation> annotation) {
        return Arrays.stream(clazz.getDeclaredFields())
                .filter(method -> method.isAnnotationPresent(annotation))
                .toList();
    }

    public static List<Method> findMethodsAnnotated(Class<?> clazz, Class<? extends Annotation> annotation) {
        return Arrays.stream(clazz.getDeclaredMethods())
                .filter(method -> method.isAnnotationPresent(annotation))
                .toList();
    }

    public static List<Annotation> getTypeAnnotations(Class<?> clazz) {
        return Arrays.stream(clazz.getDeclaredAnnotations()).toList();
    }

    public static List<Constructor<?>> findConstructorsAnnotated(Class<?> clazz, Class<? extends Annotation> annotation) {
        return Arrays.stream(clazz.getDeclaredConstructors())
                .filter(method -> method.isAnnotationPresent(annotation))
                .toList();
    }

    public static List<Class<?>> getInterfaces(Class<?> clazz) {
        return Arrays.stream(clazz.getInterfaces()).toList();
    }
}
