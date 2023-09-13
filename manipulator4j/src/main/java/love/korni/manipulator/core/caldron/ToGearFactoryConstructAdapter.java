package love.korni.manipulator.core.caldron;

import love.korni.manipulator.core.caldron.metadata.GearMetadata;

import lombok.RequiredArgsConstructor;

import java.lang.reflect.Type;
import java.util.Collection;

/**
 * @author Sergei_Kornilov
 */
@RequiredArgsConstructor
public class ToGearFactoryConstructAdapter {

    private final GearFactory gearFactory;

    public <T> T getGearOrConstruct(GearMetadata gearMetadata) {
        return gearFactory.getGearOrConstruct(gearMetadata, null);
    }

    public <T> Collection<T> getGearsOrConstruct(Type type) {
        return gearFactory.getGearsOrConstruct(type);
    }

    public <T> T getGear(String gearName, Class<T> type) {
        return gearFactory.getGear(gearName, type);
    }

    public <T> T getGear(Class<T> type, Object[] args) {
        return gearFactory.getGear(type, args);
    }

    public <T> T getGear(String gearName, Type type) throws ClassNotFoundException {
        return gearFactory.getGear(gearName, type);
    }

    public <T> T getGear(Type type) {
        return gearFactory.getGear(type);
    }

}
