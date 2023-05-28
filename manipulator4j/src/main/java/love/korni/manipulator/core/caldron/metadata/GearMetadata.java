/*
 * Copyright (c) {year} Sergei Kornilov
 * Licensed under the Apache License, Version 2.0
 */

package love.korni.manipulator.core.caldron.metadata;

import love.korni.manipulator.core.caldron.GearFactory;
import love.korni.manipulator.core.caldron.GearMetadataFactory;

/**
 * Базовый интерфейс с метаинформацией о шестерне, управляемой DI контейнером.
 *
 * @author Sergei_Konilov
 */
public sealed interface GearMetadata permits AbstractGearMetadata {

    String getName();

    String getGearName();

    GearScope getScope();

    String[] getProfiles();

    GearMetadata getParent();

    Class<?> getGearClass();

    GearMetadataFactory getFactory(GearFactory gearFactory);

}
