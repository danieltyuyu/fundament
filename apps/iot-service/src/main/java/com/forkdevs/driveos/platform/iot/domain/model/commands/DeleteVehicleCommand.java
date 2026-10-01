package com.forkdevs.driveos.platform.iot.domain.model.commands;

import com.forkdevs.driveos.platform.shared.domain.model.valueobjects.VehicleId;

/**
 * Command representing the intent to delete (unregister) a Vehicle.
 */
public record DeleteVehicleCommand(VehicleId vehicleId) {
    public DeleteVehicleCommand {
        if (vehicleId == null) {
            throw new IllegalArgumentException("vehicleId cannot be null");
        }
    }
}
