package com.forkdevs.driveos.platform.iot.interfaces.rest.transform;

import com.forkdevs.driveos.platform.iot.domain.model.aggregates.Vehicle;
import com.forkdevs.driveos.platform.iot.interfaces.rest.resources.VehicleResource;

/**
 * Assembler to translate Vehicle domain aggregate to VehicleResource DTO.
 */
public class VehicleResourceFromAggregateAssembler {
    public static VehicleResource toResourceFromAggregate(Vehicle aggregate) {
        return new VehicleResource(
                aggregate.getId() != null ? aggregate.getId().value() : null,
                aggregate.getPlateNumber(),
                aggregate.getBrand(),
                aggregate.getModel(),
                aggregate.getYear(),
                aggregate.getVin()
        );
    }
}
