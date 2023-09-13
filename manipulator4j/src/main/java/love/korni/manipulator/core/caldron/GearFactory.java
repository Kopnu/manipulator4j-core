/*
 * Copyright (c) {year} Sergei Kornilov
 * Licensed under the Apache License, Version 2.0
 */

package love.korni.manipulator.core.caldron;

import love.korni.manipulator.core.caldron.metadata.ArrayGearMetadata;
import love.korni.manipulator.core.caldron.metadata.ClassGearMetadata;
import love.korni.manipulator.core.caldron.metadata.GearMetadata;
import love.korni.manipulator.core.caldron.metadata.MethodGearMetadata;
import love.korni.manipulator.core.caldron.metadata.factory.ArrayGearMetadataFactory;
import love.korni.manipulator.core.caldron.metadata.factory.ClassGearMetadataFactory;
import love.korni.manipulator.core.caldron.metadata.factory.GearMetadataFactory;
import love.korni.manipulator.core.caldron.metadata.factory.MethodGearMetadataFactory;
import love.korni.manipulator.core.exception.GearConstructionException;
import love.korni.manipulator.core.exception.NoSuchGearMetadataException;
import love.korni.manipulator.core.gear.args.ArgsGear;
import love.korni.manipulator.util.Assert;
import love.korni.manipulator.util.ReflectionUtils;

import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;

import java.lang.reflect.Modifier;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * Класс-фабрика для постройки классов из DI контейнера.
 *
 * @author Sergei_Konilov
 */
@Slf4j
public class GearFactory {

    private final Map<String, Object> singletonByNameGears = new ConcurrentHashMap<>(64);
    private final Map<Class<?>, Object> singletonByClassGears = new ConcurrentHashMap<>(64);
    private final Map<String, GearMetadata> gearMetadataByNameMap = new ConcurrentHashMap<>(64);
    private final Map<Class<?>, GearMetadata> gearMetadataByClassMap = new ConcurrentHashMap<>(64);

    private final List<String> singletonsCurrentlyInPreCreation = Collections.synchronizedList(new LinkedList<>());
    private final List<String> singletonsCurrentlyInCreation = Collections.synchronizedList(new LinkedList<>());
    private final List<String> singletonsCurrentlyInPostCreation = Collections.synchronizedList(new LinkedList<>());

    private final Map<Class<? extends GearMetadata>, GearMetadataFactory<? extends GearMetadata>> gearMetadataFactories = new ConcurrentHashMap<>(Map.of(
            ClassGearMetadata.class, new ClassGearMetadataFactory(this),
            MethodGearMetadata.class, new MethodGearMetadataFactory(this),
            ArrayGearMetadata.class, new ArrayGearMetadataFactory(this)
    ));

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
        log.trace("Register singleton [{}]: {}", name, gear);
        registerGear(name, gear);
    }

    public <T> T getGear(Class<T> type) {
        GearMetadata gearMetadata = getMetadata(type, null);
        if (gearMetadata == null) {
            throw new NoSuchGearMetadataException("Unknown gear with type [%s]".formatted(type));
        }
        return getGearOrConstruct(gearMetadata, null);
    }

    @SneakyThrows
    public <T> T getGear(Type type) {
        String typeName = type.getTypeName();
        if (type instanceof ParameterizedType parameterizedType) {
            if (ReflectionUtils.isCollectionType(type)) {
                Type[] actualTypes = parameterizedType.getActualTypeArguments();
                Type actualType = actualTypes[0];
                return (T) getGearsOrConstruct(actualType);
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
        return getGearOrConstruct(gearMetadata, args);
    }

    public <T> T getGear(String gearName, Class<T> type) {
        GearMetadata gearMetadata = getMetadata(type, gearName);
        if (gearMetadata == null) {
            throw new NoSuchGearMetadataException("Unknown gear with name [%s] and type [%s]".formatted(gearName, type));
        }
        return getGearOrConstruct(gearMetadata, null);
    }

    protected <T> T getGear(String gearName, Type type) throws ClassNotFoundException {
        Class<?> loadClass = Thread.currentThread().getContextClassLoader().loadClass(type.getTypeName());
        GearMetadata gearMetadata = getMetadata(loadClass, gearName);
        if (gearMetadata == null) {
            throw new NoSuchGearMetadataException("Unknown gear with name [%s] and type [%s]".formatted(gearName, type));
        }
        return getGearOrConstruct(gearMetadata, null);
    }

    protected <T> T getGearOrConstruct(GearMetadata gearMetadata, Object[] args) {
        Object newGear = switch (gearMetadata.getScope()) {
            case SINGLETON -> {
                Object gear = singletonByNameGears.get(gearMetadata.getGearName());
                if (gear == null) {
                    gear = singletonByClassGears.get(gearMetadata.getGearClass());
                }
                if (gear != null) {
                    yield gear;
                }

                preConstruct(gearMetadata);
                gear = constructGear(gearMetadata, args);
                registerGear(gearMetadata, gear);
                yield gear;
            }
            case PROTOTYPE -> {
                preConstruct(gearMetadata);
                yield constructGear(gearMetadata, args);
            }
        };

        postConstruct(gearMetadata, newGear);
        return (T) newGear;
    }

    @SneakyThrows
    protected <T> Collection<T> getGearsOrConstruct(Type type) {
        Class<T> loadClass = (Class<T>) Thread.currentThread().getContextClassLoader().loadClass(type.getTypeName());
        return getGearsOrConstruct(loadClass);
    }

    protected <T> Collection<T> getGearsOrConstruct(Class<T> type) {
        GearMetadata gearMetadata = getMetadata(type, null, true);
        return new ArrayList<>((Collection<T>) getFactory(gearMetadata).construct(gearMetadata, null));
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
                    List<GearMetadata> primaryMetadataList = gearMetadataResolve.stream().filter(GearMetadata::isPrimary).toList();
                    if (primaryMetadataList.isEmpty()) {
                        StringBuilder sb = new StringBuilder("\n");
                        gearMetadataResolve.forEach(gmr -> sb.append("\t-\t").append(gmr.getGearName()).append(" (").append(gmr.getGearClass()).append(")").append("\n"));
                        throw new NoSuchGearMetadataException("So much candidates for autoinjected gear [%s] by name: '%s'.\nTips:\n\t-\tTry to specify gear name.\n\t-\tTry to setup primary = true.\n%s".formatted(clazz, name, sb));
                    } else if (primaryMetadataList.size() != 1) {
                        StringBuilder sb = new StringBuilder("\n");
                        primaryMetadataList.forEach(gmr -> sb.append("\t-\t").append(gmr.getGearName()).append(" (").append(gmr.getGearClass()).append(")").append("\n"));
                        throw new NoSuchGearMetadataException("So much 'Primary' candidates for autoinjected gear [%s] by name: '%s'.\nTips:\n\t-\tLeave only one Gear with primary = true.\n%s".formatted(clazz, name, sb));
                    } else {
                        gearMetadata = primaryMetadataList.get(0);
                    }
                } else {
                    gearMetadata = new ArrayGearMetadata(clazz, gearMetadataResolve);
                }
            } else if (gearMetadataResolve.size() == 1) {
                if (isManyResults) {
                    gearMetadata = new ArrayGearMetadata(clazz, gearMetadataResolve);
                } else {
                    gearMetadata = gearMetadataResolve.get(0);
                }
            }
        }

        gearMetadata = filterByProfilesActive(gearMetadata);

        return gearMetadata;
    }

    private GearMetadata filterByProfilesActive(GearMetadata gearMetadata) {
        if (gearMetadata != null) {
            ArgsGear argsGear = (ArgsGear) singletonByNameGears.get("argsgear");
            List<String> activeProfiles = new ArrayList<>(List.of("default"));
            List<String> profiles = Optional.ofNullable(argsGear.getOptionValues("profiles")).orElse(Collections.emptyList());
            activeProfiles.addAll(profiles);

            List<String> gearProfiles = List.of(gearMetadata.getProfiles());
            if (CollectionUtils.containsAny(gearProfiles, activeProfiles)) {
                return gearMetadata;
            }
        }
        return null;
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

    private void preConstruct(GearMetadata gearMetadata) {
        synchronized (singletonsCurrentlyInPreCreation) {
            String gearInPreCreation = gearMetadata.getGearClass() + " " + gearMetadata.getGearName();
            if (singletonsCurrentlyInPostCreation.contains(gearInPreCreation)) {
                return;
            }
            singletonsCurrentlyInPostCreation.add(gearInPreCreation);
            getFactory(gearMetadata).preConstruct(gearMetadata);
            singletonsCurrentlyInPostCreation.remove(gearInPreCreation);
        }
    }

    private Object constructGear(GearMetadata gearMetadata, Object[] args) {
        synchronized (singletonsCurrentlyInCreation) {
            String gearInCreation = gearMetadata.getGearClass() + " " + gearMetadata.getGearName();
            if (singletonsCurrentlyInCreation.contains(gearInCreation)) {
                throw new GearConstructionException("Gear with type [%s] and name [%s] currently in construction.\n\tCircular reference?\n%s"
                        .formatted(gearMetadata.getGearClass(), gearMetadata.getGearName(), GearFactoryUtils.resolveCircularRef(singletonsCurrentlyInCreation, gearInCreation)));
            }
            singletonsCurrentlyInCreation.add(gearInCreation);

            Object gear = getFactory(gearMetadata).construct(gearMetadata, args);

            if (gear == null) {
                throw new GearConstructionException("Can not construct a gear with type [" + gearMetadata.getGearClass() + "]");
            }
            singletonsCurrentlyInCreation.remove(gearInCreation);
            return gear;
        }
    }

    private void postConstruct(GearMetadata gearMetadata, Object object) {
        synchronized (singletonsCurrentlyInPostCreation) {
            String gearInPostCreation = gearMetadata.getGearClass() + " " + gearMetadata.getGearName();
            if (singletonsCurrentlyInPostCreation.contains(gearInPostCreation)) {
                return;
            }
            singletonsCurrentlyInPostCreation.add(gearInPostCreation);
            getFactory(gearMetadata).postConstruct(gearMetadata, object);
            singletonsCurrentlyInPostCreation.remove(gearInPostCreation);
        }
    }

    private GearMetadataFactory getFactory(GearMetadata gearMetadata) {
        return gearMetadataFactories.get(gearMetadata.getClass());
    }
}
