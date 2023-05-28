/*
 * Copyright (c) {year} Sergei Kornilov
 * Licensed under the Apache License, Version 2.0
 */

package love.korni.manipulator.core.gear.args;

import love.korni.manipulator.util.Assert;

import java.util.List;
import java.util.Set;

/**
 * @author Sergei_Konilov
 */
public class DefaultArgsGear implements ArgsGear {

    private final String[] args;
    private final CommandLineArgs commandLineArgs;

    public DefaultArgsGear(String[] args) {
        Assert.notNull(args, "'args' must not be null");
        this.args = args;
        this.commandLineArgs = new SimpleCommandLineArgsParser().parse(args);
    }

    @Override
    public String[] getSourceArgs() {
        return this.args;
    }

    @Override
    public Set<String> getOptionNames() {
        return this.commandLineArgs.getOptionNames();
    }

    @Override
    public boolean containsOption(String name) {
        return this.commandLineArgs.containsOption(name);
    }

    @Override
    public List<String> getOptionValues(String name) {
        return this.commandLineArgs.getOptionValues(name);
    }

    @Override
    public List<String> getNonOptionArgs() {
        return this.commandLineArgs.getNonOptionArgs();
    }
}
