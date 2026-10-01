package com.forkdevs.driveos.platform.iot.interfaces.rest.transform;

import com.forkdevs.driveos.platform.iot.domain.model.commands.UpdateVehicleCommand;
import com.forkdevs.driveos.platform.iot.interfaces.rest.resources.UpdateVehicleResource;

import java.util.UUID;

/**
 * Assembler class responsible for transforming an update resource and vehicle ID into a domain command.
 */
public final class UpdateVehicleCommandFromResourceAssembler {

    private UpdateVehicleCommandFromResourceAssembler() {}

    /**
     * Creates a new UpdateVehicleCommand from a resource and path variable ID.
     * @param id the unique identifier of the vehicle
     * @param resource the update resource
     * @return the assembled UpdateVehicleCommand
     */
    public static UpdateVehicleCommand toCommandFromResource(UUID id, UpdateVehicleResource resource) {
        return new UpdateVehicleCommand(
                id,
                resource.plateNumber(),
                resource.brand(),
                resource.model(),
                resource.year(),
                resource.vin()
        );
    }
}
