/*
 * Copyright (c) {year} Sergei Kornilov
 * Licensed under the Apache License, Version 2.0
 */

package love.korni.manipulator;

import static org.reflections.scanners.Scanners.values;

import love.korni.manipulator.core.annotation.Autoinject;
import love.korni.manipulator.core.annotation.Gear;
import love.korni.manipulator.core.caldron.ApplicationCaldron;
import love.korni.manipulator.core.caldron.Caldron;
import love.korni.manipulator.core.caldron.GearFactory;
import love.korni.manipulator.core.caldron.metadata.ClassGearMetadata;
import love.korni.manipulator.core.caldron.metadata.GearMetadata;
import love.korni.manipulator.core.caldron.metadata.MethodGearMetadata;
import love.korni.manipulator.core.exception.GearConstructionException;
import love.korni.manipulator.core.gear.args.ArgsGear;
import love.korni.manipulator.core.gear.args.DefaultArgsGear;
import love.korni.manipulator.core.gear.file.FileManager;
import love.korni.manipulator.core.gear.file.ResourceFileManager;
import love.korni.manipulator.logging.LoggerConfigurer;

import lombok.extern.slf4j.Slf4j;
import love.korni.manipulator.core.runner.Runner;
import love.korni.manipulator.util.ConstructionUtils;
import love.korni.manipulator.util.ReflectionUtils;

import org.apache.commons.lang3.StringUtils;
import org.reflections.Reflections;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Modifier;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * DI container. Entry endpoint for using manipulator4j.
 *
 * @author Sergei_Konilov
 */
@Slf4j
public class Manipulator {

    private final Reflections reflections;

    public static Caldron run(Class<?> mainClass) {
        return new Manipulator(mainClass).manipulate();
    }

    public static Caldron run(String packageName) {
        return new Manipulator(packageName).manipulate();
    }

    public static Caldron run(Class<?> mainClass, String[] args) {
        return new Manipulator(mainClass).manipulate(args);
    }

    private Manipulator(Class<?> mainClass) {
        this.reflections = new Reflections(mainClass.getPackageName(), values());
    }

    private Manipulator(String packageName) {
        this.reflections = new Reflections(packageName, values());
    }

    private Caldron manipulate() {
        return manipulate(new String[0]);
    }

    private Caldron manipulate(String[] args) {
        log.info("Manipulator4j configuration starting");

        log.debug("Class tree analysis");
        Set<GearMetadata> gearMetadataSet = new HashSet<>();
        gearMetadataSet.addAll(reflections.getMethodsAnnotatedWith(Gear.class).parallelStream().map(MethodGearMetadata::new).collect(Collectors.toSet()));
        gearMetadataSet.addAll(reflections.getTypesAnnotatedWith(Gear.class).parallelStream().map(ClassGearMetadata::new).collect(Collectors.toSet()));

        log.debug("Checking the correctness of the use of annotations");
        checkAnnotationsUsage();

        log.debug("Setup DI container");
        GearFactory gearFactory = new GearFactory(gearMetadataSet);

        FileManager fileManager = new ResourceFileManager();
        gearFactory.registerSingleton("filemanager", fileManager);

        LoggerConfigurer loggerConfigurer = new LoggerConfigurer(fileManager);
        loggerConfigurer.configure("base-manipulator.yml");

        String banner = fileManager.readFileAsString(ResourceFileManager.CLASSPATH + "banner.txt");
        log.info(banner);

        log.debug("Launching Runners");
        ArgsGear argsGear = new DefaultArgsGear(args);
        gearFactory.registerSingleton("argsgear", argsGear);
        runRunners(args);

        ApplicationCaldron applicationCaldron = new ApplicationCaldron(gearFactory);
        gearFactory.registerSingleton(applicationCaldron);

        log.info("Manipulator4j configuration completed!");
        return applicationCaldron;
    }

    private void checkAnnotationsUsage() {
        Set<Class<?>> classes = Stream.concat(
                        Stream.concat(
                                reflections.getFieldsAnnotatedWith(Autoinject.class).stream().map(field -> (Class<?>) field.getDeclaringClass()),
                                reflections.getMethodsAnnotatedWith(Autoinject.class).stream().map(method -> (Class<?>) method.getDeclaringClass())),
                        reflections.getConstructorsAnnotatedWith(Autoinject.class).stream().map(constructor -> (Class<?>) constructor.getDeclaringClass()))
                .filter(clazz -> !clazz.isAnnotationPresent(Gear.class))
                .collect(Collectors.toSet());
        if (!classes.isEmpty()) {
            log.warn("These classes use @Autoinject annotation but are not marked as @Gear: {}", classes);
        }
    }

    private void runRunners(String... args) {
        Set<Runner> runners = reflections.getSubTypesOf(Runner.class).stream()
            .filter(runner -> !Modifier.isAbstract(runner.getModifiers()))
            .filter(runner -> !Modifier.isInterface(runner.getModifiers()))
            .map(runner -> {
                try {
                    return (Runner) ConstructionUtils.useDefaultConstructor(runner);
                } catch (InvocationTargetException | InstantiationException | IllegalAccessException e) {
                    log.error("Exception while creating a Runner", e);
                }
                return null;
            })
            .filter(Objects::nonNull)
            .collect(Collectors.toSet());
        runners.forEach(runner -> runner.run(args));
    }
}
