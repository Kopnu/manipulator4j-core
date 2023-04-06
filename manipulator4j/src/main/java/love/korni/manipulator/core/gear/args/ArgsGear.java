/*
 * Copyright (c) {year} Sergei Kornilov
 * Licensed under the Apache License, Version 2.0
 */

package love.korni.manipulator.core.gear.args;

import java.util.List;
import java.util.Set;

/**
 * ArgsComponent
 *
 * @author Sergei_Konilov
 */
public interface ArgsGear {

    String[] getSourceArgs();

    Set<String> getOptionNames();

    boolean containsOption(String name);

    List<String> getOptionValues(String name);

    List<String> getNonOptionArgs();
}
