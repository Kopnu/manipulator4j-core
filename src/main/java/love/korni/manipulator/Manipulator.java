/*
 * Copyright (c) {year} Sergei Kornilov
 * Licensed under the Apache License, Version 2.0
 */

package love.korni.manipulator;

import static org.reflections.scanners.Scanners.values;

import love.korni.manipulator.core.caldron.GearFactory;
import love.korni.manipulator.core.caldron.metadata.GearMetadata;
import love.korni.manipulator.core.gear.args.ArgsGear;
import love.korni.manipulator.core.gear.args.DefaultArgsGear;
import love.korni.manipulator.core.annotation.Gear;
import love.korni.manipulator.core.caldron.ApplicationCaldron;
import love.korni.manipulator.core.caldron.Caldron;
import love.korni.manipulator.core.caldron.metadata.ClassGearMetadata;
import love.korni.manipulator.core.caldron.metadata.MethodGearMetadata;

import org.reflections.Reflections;

import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * DI container. Entry endpoint for using manipulator4j.
 *
 * @author Sergei_Konilov
 */
public class Manipulator {

    private Reflections reflections;

    public static Caldron run(Class<?> mainClass) {
        return new Manipulator(mainClass).manipulate();
    }

    public static Caldron run(Class<?> mainClass, String[] args) {
        return new Manipulator(mainClass).manipulate(args);
    }

    public Manipulator(Class<?> mainClass) {
        this.reflections = new Reflections(mainClass.getPackageName(), values());
    }

    private Caldron manipulate() {
        return manipulate(new String[0]);
    }

    private Caldron manipulate(String[] args) {
        Set<GearMetadata> gearMetadataSet = new HashSet<>();
        gearMetadataSet.addAll(reflections.getMethodsAnnotatedWith(Gear.class).parallelStream().map(MethodGearMetadata::new).collect(Collectors.toSet()));
        gearMetadataSet.addAll(reflections.getTypesAnnotatedWith(Gear.class).parallelStream().map(ClassGearMetadata::new).collect(Collectors.toSet()));

        GearFactory gearFactory = new GearFactory(gearMetadataSet);

        ArgsGear argsGear = new DefaultArgsGear(args);
        gearFactory.registerSingleton("argsgear", argsGear);

        // Запустить CommandLineRunner'ы
        // foreach commandLineRunner.run(args);
        //ToDo: Реализация CommandRunner

        ApplicationCaldron applicationCaldron = new ApplicationCaldron(gearFactory);
        gearFactory.registerSingleton(applicationCaldron);
        return applicationCaldron;
    }

}
