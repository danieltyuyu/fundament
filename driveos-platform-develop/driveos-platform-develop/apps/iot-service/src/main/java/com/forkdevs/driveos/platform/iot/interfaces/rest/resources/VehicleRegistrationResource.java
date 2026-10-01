package com.forkdevs.driveos.platform.iot.interfaces.rest.resources;

import java.time.Instant;
import java.util.UUID;

/**
 * Resource representing the response structure of a Vehicle Registration.
 */
public record VehicleRegistrationResource(
        UUID id,
        UUID userId,
        UUID vehicleId,
        String status,
        Instant createdAt
) {
}
