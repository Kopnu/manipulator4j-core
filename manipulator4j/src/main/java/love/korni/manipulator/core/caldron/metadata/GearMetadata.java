/*
 * Copyright (c) {year} Sergei Kornilov
 * Licensed under the Apache License, Version 2.0
 */

package love.korni.manipulator.core.caldron.metadata;

/**
 * Базовый интерфейс с метаинформацией о шестерне, управляемой DI контейнером.
 *
 * @author Sergei_Konilov
 */
public sealed interface GearMetadata permits AbstractGearMetadata {

    String getName();

    String getGearName();

    GearScope getScope();

    boolean isPrimary();

    String[] getProfiles();

    GearMetadata getParent();

    Class<?> getGearClass();

}
