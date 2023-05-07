/*
 * Copyright (c) {year} Sergei Kornilov
 * Licensed under the Apache License, Version 2.0
 */

package love.korni.manipulator.core.caldron.metadata;

import love.korni.manipulator.core.annotation.Gear;
import love.korni.manipulator.core.caldron.GearFactory;
import love.korni.manipulator.core.caldron.GearMetadataFactory;
import love.korni.manipulator.core.exception.GearConstructionException;
import org.apache.commons.lang3.StringUtils;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Arrays;

/**
 * Реализация {@link ArrayGearMetadata}, хранящая в себе метаинформацию шестерни, построенной, когда аннотация {@link Gear} на методе.
 *
 * @author Sergei_Konilov
 */
public class MethodGearMetadata extends AbstractGearMetadata {

    private final Method method;

    public MethodGearMetadata(Method method) {
        super(name(method), method.getReturnType());
        this.method = method;
        this.parent = new ClassGearMetadata(method.getDeclaringClass());
    }

    public Method getMethod() {
        return method;
    }

    @Override
    public GearMetadataFactory getFactory(GearFactory gearFactory) {
        return getFactory(() -> new GearMetadataFactory(gearFactory) {
            @Override
            public Object construct(Object[] args) throws GearConstructionException {
                try {
                    Object parent = getGear(getParent());
                    Object[] params = args != null ? args : getParams();
                    return method.invoke(parent, params);
                } catch (IllegalAccessException | InvocationTargetException e) {
                    throw new GearConstructionException(e);
                }
            }

            private Object[] getParams() {
                Method method = getMethod();
                Class<?>[] parameterTypes = method.getParameterTypes();
                return Arrays.stream(parameterTypes).map(gearFactory::getGear).toArray();
            }
        });
    }

    private static String name(Method method) {
        String gearValue = method.getAnnotation(Gear.class).value();
        if (StringUtils.isBlank(gearValue)) {
            gearValue = method.getName();
        }
        return gearValue;
    }
}
