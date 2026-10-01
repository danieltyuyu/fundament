package com.forkdevs.driveos.platform.iot.domain.model.commands;

import com.forkdevs.driveos.platform.iot.domain.model.valueobjects.Obd2DeviceId;

/**
 * Command representing the intent to delete (unregister) an OBD2 Device.
 */
public record DeleteObd2DeviceCommand(Obd2DeviceId obd2DeviceId) {
    public DeleteObd2DeviceCommand {
        if (obd2DeviceId == null) {
            throw new IllegalArgumentException("obd2DeviceId cannot be null");
        }
    }
}
