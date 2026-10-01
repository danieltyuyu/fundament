package com.forkdevs.driveos.platform.iot.interfaces.rest.resources;

import java.time.Instant;
import java.util.UUID;

/**
 * Resource representing the response structure of an OBD2 Device Registration.
 */
public record Obd2DeviceRegistrationResource(
        UUID id,
        UUID obd2DeviceId,
        UUID branchId,
        UUID vehicleId,
        String status,
        Instant createdAt
) {
}
