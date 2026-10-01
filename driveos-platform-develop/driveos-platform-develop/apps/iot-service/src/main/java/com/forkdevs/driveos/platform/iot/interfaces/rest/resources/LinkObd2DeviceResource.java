package com.forkdevs.driveos.platform.iot.interfaces.rest.resources;

import jakarta.validation.constraints.NotNull;
import java.util.UUID;

/**
 * Resource representing the request body to link an OBD2 device to a vehicle.
 */
public record LinkObd2DeviceResource(
        @NotNull(message = "OBD2 device ID is required")
        UUID obd2DeviceId,

        @NotNull(message = "Branch ID is required")
        UUID branchId,

        @NotNull(message = "Vehicle ID is required")
        UUID vehicleId
) {
}
