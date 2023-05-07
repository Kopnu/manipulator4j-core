/*
 * Copyright (c) {year} Sergei Kornilov
 * Licensed under the Apache License, Version 2.0
 */

package love.korni.manipulator.core.caldron.metadata;

import static love.korni.manipulator.core.caldron.GearFactoryUtils.getGearAnnotationValue;

import love.korni.manipulator.core.annotation.Autoinject;
import love.korni.manipulator.core.annotation.Gear;
import love.korni.manipulator.core.caldron.GearFactory;
import love.korni.manipulator.core.caldron.GearMetadataFactory;
import love.korni.manipulator.core.exception.GearConstructionException;
import love.korni.manipulator.core.exception.NoSuchGearMetadataException;
import love.korni.manipulator.util.ConstructionUtils;
import love.korni.manipulator.util.ReflectionUtils;

import lombok.Getter;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

/**
 * Реализация {@link ArrayGearMetadata}, хранящая в себе метаинформацию шестерни, построенной, когда аннотация {@link Gear} на классе.
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
    public GearMetadataFactory getFactory(GearFactory gearFactory) {
        return getFactory(() -> new GearMetadataFactory(gearFactory) {
            @Override
            public Object construct(Object[] args) throws GearConstructionException {
                Object gear;
                try {
                    List<Constructor<?>> constructorsAnnotated = getConstructorsAnnotated();
                    int size = constructorsAnnotated.size();
                    if (args != null) {
                        gear = ConstructionUtils.useConstructorWithArgs(gearClass, args);
                    } else {
                        gear = switch (size) {
                            case 0 -> ConstructionUtils.useDefaultConstructor(gearClass);
                            case 1 -> useAutoinjectConstructor();
                            default -> throw new GearConstructionException("Found %d \"@Autoinjected\" constructors. Expected one.".formatted(size));
                        };
                    }
                    List<Field> fieldsAnnotated = getFieldsAnnotated();
                    for (Field field : fieldsAnnotated) {
                        try {
                            String injectGear = field.getAnnotation(Autoinject.class).value();
                            ReflectionUtils.makeAccessible(field);
                            Class<?> fieldType = field.getType();
                            Object value;
                            if (Collection.class.isAssignableFrom(fieldType)) {
                                ParameterizedType genericType = (ParameterizedType) field.getGenericType();
                                Type typeArgument = genericType.getActualTypeArguments()[0];
                                value = getGears(typeArgument);
                            } else {
                                String name = injectGear.isBlank() ? field.getName() : injectGear;
                                value = getGear(name, fieldType);
                            }

                            try {
                                field.set(gear, value);
                            } catch (IllegalAccessException e) {
                                throw new IllegalStateException("Could not access method or field: " + e.getMessage(), e);
                            } catch (IllegalArgumentException e) {
                                throw new GearConstructionException("Error while construction a gear", e);
                            }
                        } catch (NoSuchGearMetadataException e) {
                            throw new GearConstructionException("Error while construction a gear " + gear.getClass(), e);
                        }
                    }
                } catch (IllegalAccessException | InvocationTargetException | InstantiationException e) {
                    throw new GearConstructionException(e);
                }
                return gear;
            }


            private Object useAutoinjectConstructor() throws InvocationTargetException, InstantiationException, IllegalAccessException {
                Constructor<?> constructor = constructorsAnnotated.get(0);
                Class<?>[] parameterTypes = constructor.getParameterTypes();
                Object[] params = Arrays.stream(parameterTypes).map(gearFactory::getGear).toArray();
                return constructor.newInstance(params);
            }

        });
    }
}
