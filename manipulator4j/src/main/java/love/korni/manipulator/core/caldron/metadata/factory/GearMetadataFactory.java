package love.korni.manipulator.core.caldron.metadata.factory;

import love.korni.manipulator.core.caldron.metadata.GearMetadata;
import love.korni.manipulator.core.exception.GearConstructionException;

/**
 * @author Sergei_Kornilov
 */
public interface GearMetadataFactory<G extends GearMetadata> {

    void preConstruct(G gearMetadata);

    Object construct(G gearMetadata, Object[] args) throws GearConstructionException;

    <T> void postConstruct(G gearMetadata, T object) throws GearConstructionException;

}
