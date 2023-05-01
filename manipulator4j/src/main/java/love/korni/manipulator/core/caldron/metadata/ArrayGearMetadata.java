/*
 * Copyright (c) {year} Sergei Kornilov
 * Licensed under the Apache License, Version 2.0
 */

package love.korni.manipulator.core.caldron.metadata;

import love.korni.manipulator.core.caldron.GearFactory;
import love.korni.manipulator.core.caldron.GearMetadataFactory;
import love.korni.manipulator.core.exception.GearConstructionException;

import lombok.Getter;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * Расширение класса {@link ClassGearMetadata}, хранящее в себе метаинформацию коллекции из {@link GearMetadata}.
 * Нужно для постройки и последующей инъекции списка однотипный классов.
 *
 * @author Sergei_Konilov
 */
public class ArrayGearMetadata extends ClassGearMetadata {

    @Getter
    private final List<GearMetadata> gearMetadatas;

    public ArrayGearMetadata(Class<?> clazz, List<GearMetadata> gearMetadatas) {
        super(clazz);
        this.gearMetadatas = gearMetadatas;
    }

    @Override
    public GearMetadataFactory getFactory(GearFactory gearFactory) {
        return getFactory(() -> new GearMetadataFactory(gearFactory) {
            @Override
            public Object construct(Object[] args) throws GearConstructionException {
                Collection<Object> gears = new ArrayList<>();
                getGearMetadatas().stream()
                        .map(GearMetadata::getGearClass)
                        .forEach(_type -> gears.add(gearFactory.getGear(_type, args)));
                return gears;
            }
        });
    }
}
