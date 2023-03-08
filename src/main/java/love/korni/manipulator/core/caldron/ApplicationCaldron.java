/*
 * Copyright (c) {year} Sergei Kornilov
 * Licensed under the Apache License, Version 2.0
 */

package love.korni.manipulator.core.caldron;

import love.korni.manipulator.core.exception.ManipulatorRuntimeException;

import java.lang.reflect.Type;

/**
 * ApplicationCaldron
 *
 * @author Sergei_Konilov
 */
public class ApplicationCaldron implements Caldron {

    private final GearFactory gearFactory;

    public ApplicationCaldron(GearFactory gearFactory) {
        this.gearFactory = gearFactory;
    }

    public <T> T getGearOfType(Class<T> type) {
        return getGearFactory().getGear(type);
    }

    public <T> T getGearOfType(Type type) {
        return getGearFactory().getGear(type);
    }

    public GearFactory getGearFactory() {
        if (gearFactory == null) {
            throw new ManipulatorRuntimeException("GearFactory is null");
        }
        return gearFactory;
    }
}
