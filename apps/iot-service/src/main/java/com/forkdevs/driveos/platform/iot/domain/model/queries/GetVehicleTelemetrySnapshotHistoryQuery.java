package com.forkdevs.driveos.platform.iot.domain.model.queries;

import com.forkdevs.driveos.platform.shared.domain.model.valueobjects.VehicleId;

/**
 * Domain query representing the request to retrieve telemetry snapshot history for a vehicle
 * starting from the start of its active registration/linking.
 */
public record GetVehicleTelemetrySnapshotHistoryQuery(
        VehicleId vehicleId
) {
    public GetVehicleTelemetrySnapshotHistoryQuery {
        if (vehicleId == null) {
            throw new IllegalArgumentException("vehicleId cannot be null");
        }
    }
}
