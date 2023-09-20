/*
 * Copyright (c) {year} Sergei Kornilov
 * Licensed under the Apache License, Version 2.0
 */

package love.korni.manipulator.core.caldron;

import love.korni.manipulator.core.annotation.Gear;

import java.util.ArrayList;
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

    public static List<String> resolveNames(Set<String> strings, String name) {
        List<String> names = new ArrayList<>();
        if (RESOLVABLE_NAME.containsKey(name)) {
            names = RESOLVABLE_NAME.get(name);
        } else {
            List<String> founded = strings.stream().filter(str -> str.startsWith(name) || str.endsWith(name)).toList();
            if (!founded.isEmpty()) {
                names.addAll(founded);
                RESOLVABLE_NAME.put(name, names);
            }
        }
        return names;
    }

    public static String resolveCircularRef(List<String> singletonsCurrentlyInCreation, String name) {
        singletonsCurrentlyInCreation.add(name);
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < singletonsCurrentlyInCreation.size(); i++) {
            String str = singletonsCurrentlyInCreation.get(i);
            sb.append("   ").append(str).append("\n").append("      |").append("\n");
            if (i == singletonsCurrentlyInCreation.size() - 1) {
                sb.append("""
                        ^_____|
                        """);
            }
        }
        return sb.toString();
    }

    public static String getGearAnnotationValue(Gear gearAnnotation, String defaultValue) {
        if (gearAnnotation != null) {
            return gearAnnotation.value();
        }
        return defaultValue;
    }

}
