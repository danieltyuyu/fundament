package com.forkdevs.driveos.platform.iot.interfaces.rest.transform;

import com.forkdevs.driveos.platform.iot.domain.model.aggregates.Obd2Device;
import com.forkdevs.driveos.platform.iot.interfaces.rest.resources.Obd2DeviceResource;

/**
 * Assembler to translate Obd2Device domain aggregate to Obd2DeviceResource DTO.
 */
public class Obd2DeviceResourceFromAggregateAssembler {
    public static Obd2DeviceResource toResourceFromAggregate(Obd2Device aggregate) {
        return new Obd2DeviceResource(
                aggregate.getId() != null ? aggregate.getId().value() : null,
                aggregate.getBranchId() != null ? aggregate.getBranchId().value() : null,
                aggregate.getMacAddress(),
                aggregate.getStatus() != null ? aggregate.getStatus().value() : null
        );
    }
}
