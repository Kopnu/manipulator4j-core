/*
 * Copyright (c) {year} Sergei Kornilov
 * Licensed under the Apache License, Version 2.0
 */

package love.korni.manipulator.core.gear.args;

import java.util.List;
import java.util.Set;

/**
 * @author Sergei_Konilov
 */
public interface ArgsGear {

    /**
     * @return исходный массив аргументов
     */
    String[] getSourceArgs();

    Set<String> getOptionNames();

    boolean containsOption(String name);

    /**
     * Возвращает value аргумента по паттерну "--name=value"
     *
     * @return значение аргумента
     */
    List<String> getOptionValues(String name);

    List<String> getNonOptionArgs();
}
