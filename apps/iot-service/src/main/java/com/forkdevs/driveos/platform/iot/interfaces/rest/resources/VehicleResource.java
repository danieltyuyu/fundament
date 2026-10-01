package com.forkdevs.driveos.platform.iot.interfaces.rest.resources;

import java.util.UUID;

/**
 * Resource representing a Vehicle returned in REST responses.
 */
public record VehicleResource(
        UUID id,
        String plateNumber,
        String brand,
        String model,
        Integer year,
        String vin
) {
}
