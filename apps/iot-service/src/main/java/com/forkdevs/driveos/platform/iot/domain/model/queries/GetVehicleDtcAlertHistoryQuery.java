package com.forkdevs.driveos.platform.iot.domain.model.queries;

import com.forkdevs.driveos.platform.shared.domain.model.valueobjects.VehicleId;

/**
 * Domain query representing the request to retrieve historical DTC alerts for a vehicle
 * starting from the start of its active driver registration.
 */
public record GetVehicleDtcAlertHistoryQuery(
        VehicleId vehicleId
) {
    public GetVehicleDtcAlertHistoryQuery {
        if (vehicleId == null) {
            throw new IllegalArgumentException("vehicleId cannot be null");
        }
    }
}
