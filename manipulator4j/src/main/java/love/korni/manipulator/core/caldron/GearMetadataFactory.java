package love.korni.manipulator.core.caldron;

import love.korni.manipulator.core.caldron.metadata.GearMetadata;
import love.korni.manipulator.core.exception.GearConstructionException;

import lombok.RequiredArgsConstructor;

import java.lang.reflect.Type;
import java.util.Collection;

/**
 * @author Sergei_Kornilov
 */
@RequiredArgsConstructor
public abstract class GearMetadataFactory {

    private final GearFactory gearFactory;

    public abstract Object construct(Object[] args) throws GearConstructionException;

    public <T> T getGear(GearMetadata gearMetadata) {
        return gearFactory.getGear(gearMetadata, null);
    }

    public <T> T getGear(String gearName, Class<T> type) {
        return gearFactory.getGear(gearName, type);
    }

    public <T> Collection<T> getGears(Type type) {
        return gearFactory.getGears(type);
    }

}
