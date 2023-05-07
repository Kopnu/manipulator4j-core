package love.korni.manipulator.util;

import lombok.experimental.UtilityClass;
import love.korni.manipulator.core.exception.GearConstructionException;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.Arrays;

/**
 * ConstructionUtils
 */
@UtilityClass
public class ConstructionUtils {

    public Object useDefaultConstructor(Class<?> clazz) throws InvocationTargetException, InstantiationException, IllegalAccessException {
        try {
            Constructor<?> constructor = clazz.getDeclaredConstructor();
            ReflectionUtils.makeAccessible(constructor);
            return constructor.newInstance();
        } catch (NoSuchMethodException e) {
            throw new GearConstructionException("Can not find default public declared constructor for class " + clazz, e);
        }
    }

    public Object useConstructorWithArgs(Class<?> clazz, Object[] args) throws InvocationTargetException, InstantiationException, IllegalAccessException {
        try {
            Class<?>[] classes = ReflectionUtils.getClasses(args);
            Constructor<?> constructor = clazz.getDeclaredConstructor(classes);
            return constructor.newInstance(args);
        } catch (NoSuchMethodException e) {
            Class<?>[] classes = ReflectionUtils.getClasses(args);
            throw new GearConstructionException("Can not find public declared constructor with attributes: " + Arrays.toString(classes), e);
        }
    }


}
