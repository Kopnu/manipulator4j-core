/*
 * Copyright (c) {year} Sergei Kornilov
 * Licensed under the Apache License, Version 2.0
 */

package love.korni.manipulator.core.caldron.metadata;

import java.lang.reflect.Method;

/**
 * @author Sergei_Konilov
 */
public class MethodGearMetadata extends AbstractGearMetadata {

    private final Method method;

    public MethodGearMetadata(Method method) {
        super(method.getReturnType());
        this.method = method;
        this.parent = new ClassGearMetadata(method.getDeclaringClass());
    }

    @Override
    public Boolean isMethod() {
        return true;
    }

    public Method getMethod() {
        return method;
    }
}
