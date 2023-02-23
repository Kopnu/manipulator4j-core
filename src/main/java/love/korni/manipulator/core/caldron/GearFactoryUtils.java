/*
 * Copyright (c) {year} Sergei Kornilov
 * Licensed under the Apache License, Version 2.0
 */

package love.korni.manipulator.core.caldron;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

/**
 * GearFactoryUtils
 *
 * @author Sergei_Konilov
 */
public class GearFactoryUtils {

    private static final Map<String, List<String>> RESOLVABLE_NAME = new ConcurrentHashMap<>(16);

    public static List<String> resolveGearNames(Set<String> gearNames, String name) {
        if (name == null) {
            return Collections.emptyList();
        }
        if (RESOLVABLE_NAME.containsKey(name)) {
            return RESOLVABLE_NAME.get(name);
        }
        List<String> resolve = new ArrayList<>();

        for (String gearName : gearNames) {
            if (gearName.toLowerCase().contains(name.toLowerCase())) {
                resolve.add(gearName);
            }
        }

        RESOLVABLE_NAME.put(name, resolve);
        return resolve;
    }

}
