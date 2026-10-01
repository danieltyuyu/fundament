package com.forkdevs.driveos.platform.iot.domain.model.commands;

import com.forkdevs.driveos.platform.iot.domain.model.valueobjects.Obd2DeviceId;

/**
 * Command representing the intent to update an OBD2 device's details.
 */
public record UpdateObd2DeviceCommand(
        Obd2DeviceId id,
        String macAddress
) {
    public UpdateObd2DeviceCommand {
        if (id == null) {
            throw new IllegalArgumentException("id cannot be null");
        }
        if (macAddress == null || macAddress.isBlank()) {
            throw new IllegalArgumentException("macAddress cannot be null or empty");
        }
    }
}
