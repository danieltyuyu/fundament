package com.forkdevs.driveos.platform.iot.domain.model.commands;

import com.forkdevs.driveos.platform.iot.domain.model.valueobjects.Obd2DeviceRegistrationId;

/**
 * Command representing the intent to deactivate (unlink) an OBD2 Device Registration.
 */
public record DeactivateObd2DeviceRegistrationCommand(Obd2DeviceRegistrationId registrationId) {
    public DeactivateObd2DeviceRegistrationCommand {
        if (registrationId == null) {
            throw new IllegalArgumentException("registrationId cannot be null");
        }
    }
}
