package com.forkdevs.driveos.platform.iot.application.commandservices;

/**
 * Interface representing possible failure outcomes of OBD2 Device command operations.
 */
public sealed interface Obd2DeviceCommandFailure permits
        Obd2DeviceCommandFailure.NotFound,
        Obd2DeviceCommandFailure.InvalidState,
        Obd2DeviceCommandFailure.Duplicate {
    String message();
    record NotFound(String message) implements Obd2DeviceCommandFailure {}
    record InvalidState(String message) implements Obd2DeviceCommandFailure {}
    record Duplicate(String message) implements Obd2DeviceCommandFailure {}
}
