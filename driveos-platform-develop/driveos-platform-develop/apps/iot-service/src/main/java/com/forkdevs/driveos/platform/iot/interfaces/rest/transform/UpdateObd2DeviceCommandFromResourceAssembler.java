package com.forkdevs.driveos.platform.iot.interfaces.rest.transform;

import com.forkdevs.driveos.platform.iot.domain.model.commands.UpdateObd2DeviceCommand;
import com.forkdevs.driveos.platform.iot.domain.model.valueobjects.Obd2DeviceId;
import com.forkdevs.driveos.platform.iot.interfaces.rest.resources.UpdateObd2DeviceResource;

import java.util.UUID;

/**
 * Assembler class responsible for transforming an update resource and device ID into a domain command.
 */
public final class UpdateObd2DeviceCommandFromResourceAssembler {

    private UpdateObd2DeviceCommandFromResourceAssembler() {}

    /**
     * Creates a new UpdateObd2DeviceCommand from a resource and path variable ID.
     * @param id the unique identifier of the OBD2 device
     * @param resource the update resource
     * @return the assembled UpdateObd2DeviceCommand
     */
    public static UpdateObd2DeviceCommand toCommandFromResource(UUID id, UpdateObd2DeviceResource resource) {
        return new UpdateObd2DeviceCommand(
                new Obd2DeviceId(id),
                resource.macAddress()
        );
    }
}
