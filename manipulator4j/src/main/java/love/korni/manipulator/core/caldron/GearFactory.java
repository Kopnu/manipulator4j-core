/*
 * Copyright (c) {year} Sergei Kornilov
 * Licensed under the Apache License, Version 2.0
 */

package love.korni.manipulator.core.caldron;

import love.korni.manipulator.core.caldron.metadata.ArrayGearMetadata;
import love.korni.manipulator.core.caldron.metadata.ClassGearMetadata;
import love.korni.manipulator.core.caldron.metadata.GearMetadata;
import love.korni.manipulator.core.exception.GearConstructionException;
import love.korni.manipulator.core.exception.NoSuchGearMetadataException;
import love.korni.manipulator.util.Assert;

import lombok.SneakyThrows;

import java.lang.reflect.Modifier;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * Класс-фабрика для постройки классов из DI контейнера.
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
        this(Collections.emptySet());
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
        return getGear(gearMetadata, null);
    }

    @SneakyThrows
    public <T> T getGear(Type type) {
        String typeName = type.getTypeName();
        if (type instanceof ParameterizedType parameterizedType) {
            if (GearFactoryUtils.isCollectionType(type)) {
                Type[] actualTypes = parameterizedType.getActualTypeArguments();
                Type actualType = actualTypes[0];
                return (T) getGears(actualType);
            }
        }
        Class<T> loadClass = (Class<T>) Thread.currentThread().getContextClassLoader().loadClass(typeName);
        return getGear(loadClass);
    }

    public <T> T getGear(Class<T> type, Object[] args) {
        GearMetadata gearMetadata = getMetadata(type, null);
        if (gearMetadata == null) {
            throw new NoSuchGearMetadataException("Unknown gear with type [%s]".formatted(type));
        }
        return getGear(gearMetadata, args);
    }

    protected <T> T getGear(String gearName, Class<T> type) {
        GearMetadata gearMetadata = getMetadata(type, gearName);
        if (gearMetadata == null) {
            throw new NoSuchGearMetadataException("Unknown gear with name [%s] and type [%s]".formatted(gearName, type));
        }
        return getGear(gearMetadata, null);
    }

    protected <T> T getGear(String gearName, Type type) throws ClassNotFoundException {
        Class<?> loadClass = Thread.currentThread().getContextClassLoader().loadClass(type.getTypeName());
        GearMetadata gearMetadata = getMetadata(loadClass, gearName);
        if (gearMetadata == null) {
            throw new NoSuchGearMetadataException("Unknown gear with name [%s] and type [%s]".formatted(gearName, type));
        }
        return getGear(gearMetadata, null);
    }

    protected <T> T getGear(GearMetadata gearMetadata, Object[] args) {
        Object newGear = switch (gearMetadata.getType()) {
            case SINGLETON -> {
                Object gear = singletonByNameGears.get(gearMetadata.getGearName());
                if (gear == null) {
                    gear = singletonByClassGears.get(gearMetadata.getGearClass());
                }
                if (gear != null) {
                    yield gear;
                }

                gear = constructGear(gearMetadata, args);
                registerGear(gearMetadata, gear);
                yield gear;
            }
            case PROTOTYPE -> constructGear(gearMetadata, args);
        };

        return (T) newGear;
    }

    @SneakyThrows
    protected <T> Collection<T> getGears(Type type) {
        Class<T> loadClass = (Class<T>) Thread.currentThread().getContextClassLoader().loadClass(type.getTypeName());
        return getGears(loadClass);
    }

    protected <T> Collection<T> getGears(Class<T> type) {
        GearMetadata gearMetadata = getMetadata(type, null, true);
        GearMetadataFactory factory = gearMetadata.getFactory(this);
        return new ArrayList<>((Collection<T>) factory.construct(null));
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
        return getMetadata(clazz, name, false);
    }

    private GearMetadata getMetadata(Class<?> clazz, String name, boolean isManyResults) {
        Assert.notNull(clazz, "class must not be null");
        name = name != null ? name.toLowerCase() : clazz.getSimpleName().toLowerCase();

        // Поиск по Имени шестерни (название переменной или искомой шестерни)
        GearMetadata gearMetadata = gearMetadataByNameMap.get(name);

        // Поиск гира по Классу (тип переменной или искомой шестерни)
        if (gearMetadata == null) {
            gearMetadata = gearMetadataByClassMap.get(clazz);
        }

        // Поиск гира по Интерфейсу (если тип - интерфейс или абстрактный класс, то поиск реализации)
        if (gearMetadata == null && (clazz.isInterface() || Modifier.isAbstract(clazz.getModifiers()))) {
            List<GearMetadata> gearMetadataResolve = new ArrayList<>();
            gearMetadataByClassMap.keySet().stream()
                    .filter(clazz::isAssignableFrom)
                    .forEach(_class -> gearMetadataResolve.add(gearMetadataByClassMap.get(_class)));

            if (gearMetadataResolve.size() > 1) {
                if (!isManyResults) {
                    StringBuilder sb = new StringBuilder();
                    gearMetadataResolve.forEach(gmr -> sb.append(gmr.getGearName()).append("(").append(gmr.getGearClass()).append(")"));
                    throw new NoSuchGearMetadataException("So much candidates [%s] for autoinjected gear [%s]. Specify gear name.".formatted(sb, clazz));
                }
                gearMetadata = new ArrayGearMetadata(clazz, gearMetadataResolve);
            } else if (gearMetadataResolve.size() == 1) {
                if (isManyResults) {
                    gearMetadata = new ArrayGearMetadata(clazz, gearMetadataResolve);
                } else {
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

    private Object constructGear(GearMetadata gearMetadata, Object[] args) {
        synchronized (singletonsCurrentlyInCreation) {
            if (singletonsCurrentlyInCreation.contains(gearMetadata.getGearName())) {
                throw new GearConstructionException("Gear [%s] currently in construction. Circular reference? [%s]"
                        .formatted(gearMetadata.getGearName(), GearFactoryUtils.resolveCircularRef(singletonsCurrentlyInCreation, gearMetadata.getGearName())));
            }
            singletonsCurrentlyInCreation.add(gearMetadata.getGearName());

            GearMetadataFactory factory = gearMetadata.getFactory(this);
            Object gear = factory.construct(args);

            if (gear == null) {
                throw new GearConstructionException("Can not construct a gear with type [" + gearMetadata.getGearClass() + "]");
            }
            singletonsCurrentlyInCreation.remove(gearMetadata.getGearName());
            return gear;
        }
    }

}