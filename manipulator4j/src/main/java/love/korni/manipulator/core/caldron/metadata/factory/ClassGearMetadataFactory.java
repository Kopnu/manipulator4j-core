package love.korni.manipulator.core.caldron.metadata.factory;

import love.korni.manipulator.core.annotation.Autoinject;
import love.korni.manipulator.core.annotation.Specify;
import love.korni.manipulator.core.caldron.GearFactory;
import love.korni.manipulator.core.caldron.metadata.ClassGearMetadata;
import love.korni.manipulator.core.exception.GearConstructionException;
import love.korni.manipulator.core.exception.NoSuchGearMetadataException;
import love.korni.manipulator.util.ConstructionUtils;
import love.korni.manipulator.util.ReflectionUtils;

import org.apache.commons.lang3.StringUtils;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

/**
 * @author Sergei_Kornilov
 */
public class ClassGearMetadataFactory extends AbstractGearMetadataFactory<ClassGearMetadata> {

    public ClassGearMetadataFactory(GearFactory gearFactory) {
        super(gearFactory);
    }

    @Override
    public void preConstruct(ClassGearMetadata gearMetadata) {

    }

    @Override
    public Object construct(ClassGearMetadata gearMetadata, Object[] args) throws GearConstructionException {
        Object gear;
        try {
            List<Constructor<?>> constructorsAnnotated = gearMetadata.getConstructorsAutoinject();
            int size = constructorsAnnotated.size();
            if (args != null) {
                gear = ConstructionUtils.useConstructorWithArgs(gearMetadata.getGearClass(), args);
            } else {
                gear = switch (size) {
                    case 0 -> useOneDefaultConstructor(gearMetadata);
                    case 1 -> useAutoinjectConstructor(gearMetadata);
                    default -> throw new GearConstructionException("Found %d \"@Autoinjected\" constructors. Expected one.".formatted(size));
                };
            }
            if (gearMetadata.getAfterConstructMethod() != null) {
                gearMetadata.getAfterConstructMethod().invoke(gear);
            }
        } catch (IllegalAccessException | InvocationTargetException | InstantiationException e) {
            throw new GearConstructionException(e);
        }
        return gear;
    }

    @Override
    public void postConstruct(ClassGearMetadata gearMetadata, Object gear) throws GearConstructionException {
        List<Field> fieldsAnnotated = gearMetadata.getFieldsAutoinject();
        for (Field field : fieldsAnnotated) {
            try {
                ReflectionUtils.makeAccessible(field);
                Class<?> fieldType = field.getType();
                Object value;
                if (Collection.class.isAssignableFrom(fieldType)) {
                    ParameterizedType genericType = (ParameterizedType) field.getGenericType();
                    Type typeArgument = genericType.getActualTypeArguments()[0];
                    value = gearFactoryAdapter.getGearsOrConstruct(typeArgument);
                } else {
                    String name = getSpecifyGearName(field);
                    value = gearFactoryAdapter.getGear(name, fieldType);
                }

                try {
                    field.set(gear, value);
                } catch (IllegalAccessException e) {
                    throw new IllegalStateException("Could not access method or field: " + e.getMessage(), e);
                } catch (IllegalArgumentException e) {
                    throw new GearConstructionException("Error while construction a gear", e);
                }
            } catch (NoSuchGearMetadataException e) {
                throw new GearConstructionException("Error while construction a gear " + gear.getClass(), e);
            }
        }
    }

    private Object useOneDefaultConstructor(ClassGearMetadata gearMetadata) throws InvocationTargetException, InstantiationException, IllegalAccessException {
        try {
            return ConstructionUtils.useDefaultConstructor(gearMetadata.getGearClass());
        } catch (GearConstructionException e) {
            Constructor<?>[] declaredConstructors = gearMetadata.getGearClass().getDeclaredConstructors();
            if (declaredConstructors.length == 1) {
                Constructor<?> constructor = declaredConstructors[0];
                Object[] args = Arrays.stream(constructor.getGenericParameterTypes())
                        .map(gearFactoryAdapter::getGear)
                        .toArray();
                return constructor.newInstance(args);
            }
            throw e;
        }
    }

    private Object useAutoinjectConstructor(ClassGearMetadata gearMetadata) throws InvocationTargetException, InstantiationException, IllegalAccessException {
        Constructor<?> constructor = gearMetadata.getConstructorsAutoinject().get(0);
        Class<?>[] parameterTypes = constructor.getParameterTypes();
        Object[] params = Arrays.stream(parameterTypes).map(gearFactoryAdapter::getGear).toArray();
        return constructor.newInstance(params);
    }

    private String getSpecifyGearName(Field field) {
        String injectGear = field.getName();

        String value = field.getAnnotation(Autoinject.class).value();
        if (StringUtils.isNoneBlank(value)) {
            injectGear = value;
        }

        Specify specify = field.getAnnotation(Specify.class);
        if (specify != null) {
            value = specify.value();
            if (StringUtils.isNoneBlank(value)) {
                injectGear = value;
            }
        }

        return injectGear;
    }

}
