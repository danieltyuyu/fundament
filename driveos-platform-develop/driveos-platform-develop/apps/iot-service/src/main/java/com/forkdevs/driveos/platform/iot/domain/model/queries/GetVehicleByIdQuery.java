package com.forkdevs.driveos.platform.iot.domain.model.queries;

import com.forkdevs.driveos.platform.shared.domain.model.valueobjects.VehicleId;

/**
 * Query to retrieve a Vehicle by its unique identifier.
 */
public record GetVehicleByIdQuery(VehicleId vehicleId) {
}
