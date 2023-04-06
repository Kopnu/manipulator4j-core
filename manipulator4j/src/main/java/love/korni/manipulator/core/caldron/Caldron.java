/*
 * Copyright (c) {year} Sergei Kornilov
 * Licensed under the Apache License, Version 2.0
 */

package love.korni.manipulator.core.caldron;

import java.lang.reflect.Type;

/**
 * Caldron
 *
 * @author Sergei_Konilov
 */
public interface Caldron {

    <T> T getGearOfType(Class<T> type);

    <T> T getGearOfType(Type type);

    GearFactory getGearFactory();
}
