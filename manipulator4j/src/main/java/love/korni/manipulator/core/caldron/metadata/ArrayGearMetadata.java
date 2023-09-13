/*
 * Copyright (c) {year} Sergei Kornilov
 * Licensed under the Apache License, Version 2.0
 */

package love.korni.manipulator.core.caldron.metadata;

import lombok.Getter;

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

}
