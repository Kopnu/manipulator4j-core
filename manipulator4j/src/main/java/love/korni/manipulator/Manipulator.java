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
import love.korni.manipulator.core.gear.args.ArgsGear;
import love.korni.manipulator.core.gear.args.DefaultArgsGear;

import lombok.extern.slf4j.Slf4j;
import org.reflections.Reflections;

import java.util.HashSet;
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
        Set<GearMetadata> gearMetadataSet = new HashSet<>();
        gearMetadataSet.addAll(reflections.getMethodsAnnotatedWith(Gear.class).parallelStream().map(MethodGearMetadata::new).collect(Collectors.toSet()));
        gearMetadataSet.addAll(reflections.getTypesAnnotatedWith(Gear.class).parallelStream().map(ClassGearMetadata::new).collect(Collectors.toSet()));

        checkAnnotationsUsage();

        // ToDo: Отправка лога/баннера о старте конфигурации DI

        GearFactory gearFactory = new GearFactory(gearMetadataSet);

        ArgsGear argsGear = new DefaultArgsGear(args);
        gearFactory.registerSingleton("argsgear", argsGear);

        // Запустить CommandLineRunner'ы
        // foreach commandLineRunner.run(args);
        //ToDo: Реализация CommandRunner

        ApplicationCaldron applicationCaldron = new ApplicationCaldron(gearFactory);
        gearFactory.registerSingleton(applicationCaldron);

        // ToDo: Отправка лога о конце конфигурации DI

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
        log.warn("These classes use @Autoinject annotation but are not marked as @Gear: {}", classes);
    }
}
