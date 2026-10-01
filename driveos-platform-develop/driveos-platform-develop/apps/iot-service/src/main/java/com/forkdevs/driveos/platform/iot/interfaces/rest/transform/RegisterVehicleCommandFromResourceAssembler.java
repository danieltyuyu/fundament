package com.forkdevs.driveos.platform.iot.interfaces.rest.transform;

import com.forkdevs.driveos.platform.iot.domain.model.commands.RegisterVehicleCommand;
import com.forkdevs.driveos.platform.iot.interfaces.rest.resources.RegisterVehicleResource;

import java.util.UUID;

/**
 * Assembler to translate RegisterVehicleResource and userId to RegisterVehicleCommand.
 */
public class RegisterVehicleCommandFromResourceAssembler {
    public static RegisterVehicleCommand toCommandFromResource(UUID userId, RegisterVehicleResource resource) {
        return new RegisterVehicleCommand(
                userId,
                resource.plateNumber(),
                resource.brand(),
                resource.model(),
                resource.year(),
                resource.vin()
        );
    }
}
