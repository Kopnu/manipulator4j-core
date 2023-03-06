/*
 * Copyright (c) {year} Sergei Kornilov
 * Licensed under the Apache License, Version 2.0
 */

package love.korni.manipulator.core.caldron;

import love.korni.manipulator.core.annotation.Autoinject;
import love.korni.manipulator.core.caldron.metadata.ClassGearMetadata;
import love.korni.manipulator.core.caldron.metadata.GearMetadata;
import love.korni.manipulator.core.caldron.metadata.MethodGearMetadata;
import love.korni.manipulator.core.exception.GearConstructionException;
import love.korni.manipulator.core.exception.NoSuchGearMetadataException;
import love.korni.manipulator.util.Assert;
import love.korni.manipulator.util.ReflectionUtils;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * ComponentFactory
 *
 * @author Sergei_Konilov
 */
public class GearFactory {

    private final Map<String, Object> singletonByNameGears = new ConcurrentHashMap<>(64);
    private final Map<Class<?>, Object> singletonByClassGears = new ConcurrentHashMap<>(64);
    private final Map<String, GearMetadata> gearMetadataByNameMap = new ConcurrentHashMap<>(64);
    private final Map<Class<?>, GearMetadata> gearMetadataByClassMap = new ConcurrentHashMap<>(64);

    private final List<String> singletonsCurrentlyInCreation = Collections.synchronizedList(new LinkedList<>());

    public GearFactory() {
    }

    public GearFactory(Set<GearMetadata> gearMetadataSet) {
        addMetadata(gearMetadataSet);
    }

    public void registerSingleton(Object gear) {
        registerGear(gear.getClass().getSimpleName(), gear);
    }

    public void registerSingleton(String name, Object gear) {
        registerGear(name, gear);
    }

    public <T> T getGear(Class<T> type) {
        GearMetadata gearMetadata = getMetadata(type, null);
        if (gearMetadata == null) {
            throw new NoSuchGearMetadataException("Unknown gear with type [%s]".formatted(type));
        }
        return getGear(gearMetadata);
    }

    protected <T> T getGear(String gearName, Class<T> type) {
        GearMetadata gearMetadata = getMetadata(type, gearName);
        if (gearMetadata == null) {
            throw new NoSuchGearMetadataException("Unknown gear with name [%s]".formatted(gearName));
        }
        return getGear(gearMetadata);
    }

    protected <T> T getGear(GearMetadata gearMetadata) {
        Object gear = singletonByNameGears.get(gearMetadata.getGearName());
        if (gear == null) {
            gear = singletonByClassGears.get(gearMetadata.getGearClass());
        }
        if (gear != null) {
            return (T) gear;
        }

        gear = constructGear(gearMetadata);
        registerGear(gearMetadata, gear);
        return (T) gear;
    }

    private GearMetadata createMetadata(Class<?> clazz) {
        return createMetadata(clazz, clazz.getSimpleName());
    }

    private GearMetadata createMetadata(Class<?> clazz, String gearName) {
        GearMetadata parent = null;
        if (clazz.getSuperclass() != null) {
            parent = getMetadata(clazz.getSuperclass(), null);
            if (parent == null) {
                parent = createMetadata(clazz.getSuperclass());
            }
        }
        return new ClassGearMetadata(clazz, gearName, parent);
    }

    private GearMetadata getMetadata(Class<?> clazz, String name) {
        Assert.notNull(clazz, "class must not be null");
        name = name != null ? name.toLowerCase() : clazz.getSimpleName().toLowerCase();

        // Поиск по Имени шестерни (название переменной или искомой шестерни)
        GearMetadata gearMetadata = gearMetadataByNameMap.get(name);

        // Поиск гира по Классу (тип переменной или искомой шестерни)
        if (gearMetadata == null) {
            gearMetadata = gearMetadataByClassMap.get(clazz);
        }

        if (gearMetadata == null) {
            //Поиск гира по Интерфейсу (если тип - интерфейс, то поиск реализации интерфейса)
            if (clazz.isInterface()) {
                List<GearMetadata> gearMetadataResolve = new ArrayList<>();
                gearMetadataByClassMap.keySet().stream()
                        .filter(clazz::isAssignableFrom)
                        .forEach(_class -> gearMetadataResolve.add(gearMetadataByClassMap.get(_class)));

                if (gearMetadataResolve.size() > 1) {
                    StringBuilder sb = new StringBuilder();
                    gearMetadataResolve.forEach(gmr -> sb.append(gmr.getGearName()).append("(").append(gmr.getGearClass()).append(")"));
                    throw new NoSuchGearMetadataException("So much candidates [%s] for autoinjected gear [%s]. Specify gear name.".formatted(sb, clazz));
                } else if (gearMetadataResolve.size() == 1) {
                    gearMetadata = gearMetadataResolve.get(0);
                }
            }
        }

        return gearMetadata;
    }

    private void addMetadata(Set<GearMetadata> metadataSet) {
        gearMetadataByNameMap.putAll(metadataSet.stream().collect(Collectors.toMap(GearMetadata::getGearName, Function.identity())));
        gearMetadataByClassMap.putAll(metadataSet.stream().collect(Collectors.toMap(GearMetadata::getGearClass, Function.identity())));
    }

    private void registerGear(String gearName, Object gear) {
        Assert.hasText(gearName, "'gearName' must not be empty");
        Assert.notNull(gear, "'gear' must not be null");
        GearMetadata gearMetadata = gearMetadataByNameMap.get(gearName);
        if (gearMetadata == null) {
            gearMetadata = createMetadata(gear.getClass(), gearName);
        }
        registerGear(gearMetadata, gear);
    }

    private void registerGear(GearMetadata gearMetadata, Object gear) {
        Assert.notNull(gearMetadata, "'gearMetadata' must not be null");
        Assert.notNull(gear, "'gear' must not be null");
        synchronized (gearMetadataByNameMap) {
            gearMetadataByNameMap.put(gearMetadata.getGearName(), gearMetadata);
            gearMetadataByClassMap.put(gearMetadata.getGearClass(), gearMetadata);
            singletonByClassGears.putIfAbsent(gear.getClass(), gear);
            singletonByNameGears.putIfAbsent(gearMetadata.getGearName(), gear);
        }
    }

    private Object constructGear(GearMetadata gearMetadata) {
        synchronized (singletonsCurrentlyInCreation) {
            Object gear = null;

            if (singletonsCurrentlyInCreation.contains(gearMetadata.getGearName())) {
                throw new GearConstructionException("Gear [%s] currently in construction. Circular reference? [%s]"
                        .formatted(gearMetadata.getGearName(), GearFactoryUtils.resolveCircularRef(singletonsCurrentlyInCreation, gearMetadata.getGearName())));
            }

            singletonsCurrentlyInCreation.add(gearMetadata.getGearName());
            try {
                //ToDo: Переделать на паттерн, чтобы не было привязки к типу метадаты
                if (gearMetadata instanceof MethodGearMetadata methodGearMetadata) {
                    Method method = methodGearMetadata.getMethod();
                    Class<?>[] parameterTypes = method.getParameterTypes();
                    Object[] params = Arrays.stream(parameterTypes).map(this::getGear).toArray();
                    GearMetadata parent = methodGearMetadata.getParent();
                    Object parentGear = getGear(parent);
                    gear = method.invoke(parentGear, params);
                } else if (gearMetadata instanceof ClassGearMetadata classGearMetadata) {
                    List<Constructor<?>> constructorsAnnotated = classGearMetadata.getConstructorsAnnotated();
                    int size = constructorsAnnotated.size();
                    gear = switch (size) {
                        case 0 -> {
                            Constructor<?> constructor = ReflectionUtils.findDefaultConstructor(classGearMetadata.getGearClass());
                            yield constructor.newInstance();
                        }
                        case 1 -> {
                            Constructor<?> constructor = constructorsAnnotated.get(0);
                            Class<?>[] parameterTypes = constructor.getParameterTypes();
                            Object[] params = Arrays.stream(parameterTypes).map(this::getGear).toArray();
                            yield constructor.newInstance(params);
                        }
                        default -> throw new GearConstructionException("Found %d \"Autoinjected\" constructors. Expected one.".formatted(size));
                    };
                    List<Field> fieldsAnnotated = classGearMetadata.getFieldsAnnotated();
                    for (Field field : fieldsAnnotated) {
                        String injectGear = field.getAnnotation(Autoinject.class).value();
                        ReflectionUtils.makeAccessible(field);
                        Object value = getGear(injectGear.isBlank() ? field.getName() : injectGear, field.getType());
                        try {
                            field.set(gear, value);
                        } catch (IllegalAccessException e) {
                            throw new IllegalStateException("Could not access method or field: " + e.getMessage());
                        }
                    }

                }
            } catch (InstantiationException | IllegalAccessException | InvocationTargetException e) {
                throw new RuntimeException(e);
            }


            if (gear == null) {
                throw new GearConstructionException("Can not construct a gear with type [" + gearMetadata.getGearClass() + "]");
            }
            singletonsCurrentlyInCreation.remove(gearMetadata.getGearName());
            return gear;
        }
    }

}
