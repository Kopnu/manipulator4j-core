package love.korni.manipulator.core.caldron.metadata.factory;

import love.korni.manipulator.core.caldron.GearFactory;
import love.korni.manipulator.core.caldron.ToGearFactoryConstructAdapter;
import love.korni.manipulator.core.caldron.metadata.GearMetadata;

/**
 * @author Sergei_Kornilov
 */
public abstract class AbstractGearMetadataFactory<G extends GearMetadata> implements GearMetadataFactory<G> {

    protected final ToGearFactoryConstructAdapter gearFactoryAdapter;

    public AbstractGearMetadataFactory(GearFactory gearFactory) {
        this.gearFactoryAdapter = new ToGearFactoryConstructAdapter(gearFactory);
    }

}
