package love.korni.manipulator.core.caldron.metadata.factory;

import love.korni.manipulator.core.caldron.GearFactory;
import love.korni.manipulator.core.caldron.metadata.MethodGearMetadata;
import love.korni.manipulator.core.exception.GearConstructionException;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Arrays;

public class MethodGearMetadataFactory extends AbstractGearMetadataFactory<MethodGearMetadata> {

    public MethodGearMetadataFactory(GearFactory gearFactory) {
        super(gearFactory);
    }

    @Override
    public void preConstruct(MethodGearMetadata gearMetadata) {

    }

    @Override
    public Object construct(MethodGearMetadata gearMetadata, Object[] args) throws GearConstructionException {
        try {
            Object parent = gearFactoryAdapter.getGearOrConstruct(gearMetadata.getParent());
            Object[] params = args != null ? args : getParams(gearMetadata);
            return gearMetadata.getMethod().invoke(parent, params);
        } catch (IllegalAccessException | InvocationTargetException e) {
            throw new GearConstructionException(e);
        }
    }

    @Override
    public <T> void postConstruct(MethodGearMetadata gearMetadata, T object) {

    }

    private Object[] getParams(MethodGearMetadata gearMetadata) {
        Method method = gearMetadata.getMethod();
        Class<?>[] parameterTypes = method.getParameterTypes();
        return Arrays.stream(parameterTypes).map(gearFactoryAdapter::getGear).toArray();
    }
}
