package com.forkdevs.driveos.platform.iot.domain.model.commands;

import java.util.UUID;

/**
 * Command to register a new vehicle and assign its registration to a driver user.
 */
public record RegisterVehicleCommand(
        UUID userId,
        String plateNumber,
        String brand,
        String model,
        Integer year,
        String vin
) {
    public RegisterVehicleCommand {
        if (userId == null) {
            throw new IllegalArgumentException("userId cannot be null");
        }
        if (plateNumber == null || plateNumber.isBlank()) {
            throw new IllegalArgumentException("plateNumber cannot be null or empty");
        }
        if (brand == null || brand.isBlank()) {
            throw new IllegalArgumentException("brand cannot be null or empty");
        }
        if (model == null || model.isBlank()) {
            throw new IllegalArgumentException("model cannot be null or empty");
        }
        if (year == null || year <= 0) {
            throw new IllegalArgumentException("year must be a positive integer");
        }
        if (vin == null || vin.isBlank()) {
            throw new IllegalArgumentException("vin cannot be null or empty");
        }
    }
}
