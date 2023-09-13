package love.korni.manipulator.core.caldron.metadata.factory;

import love.korni.manipulator.core.caldron.GearFactory;
import love.korni.manipulator.core.caldron.metadata.ArrayGearMetadata;
import love.korni.manipulator.core.caldron.metadata.GearMetadata;
import love.korni.manipulator.core.exception.GearConstructionException;

import java.util.ArrayList;
import java.util.Collection;

/**
 * @author Sergei_Kornilov
 */
public class ArrayGearMetadataFactory extends AbstractGearMetadataFactory<ArrayGearMetadata> {

    public ArrayGearMetadataFactory(GearFactory gearFactory) {
        super(gearFactory);
    }

    @Override
    public void preConstruct(ArrayGearMetadata gearMetadata) {

    }

    @Override
    public Object construct(ArrayGearMetadata gearMetadata, Object[] args) throws GearConstructionException {
        Collection<Object> gears = new ArrayList<>();
        gearMetadata.getGearMetadatas().stream()
                .map(GearMetadata::getGearClass)
                .forEach(_type -> gears.add(gearFactoryAdapter.getGear(_type, args)));
        return gears;
    }

    @Override
    public <T> void postConstruct(ArrayGearMetadata gearMetadata, T object) {

    }

}
