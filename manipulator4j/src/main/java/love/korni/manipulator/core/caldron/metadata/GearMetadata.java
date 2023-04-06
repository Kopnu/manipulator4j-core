/*
 * Copyright (c) {year} Sergei Kornilov
 * Licensed under the Apache License, Version 2.0
 */

package love.korni.manipulator.core.caldron.metadata;

/**
 * ComponentMetadata
 *
 * @author Sergei_Konilov
 */
public interface GearMetadata {

    String getName();

    String getGearName();

    GearType getType();

    Boolean isMethod();

    GearMetadata getParent();

    Class<?> getGearClass();

}
