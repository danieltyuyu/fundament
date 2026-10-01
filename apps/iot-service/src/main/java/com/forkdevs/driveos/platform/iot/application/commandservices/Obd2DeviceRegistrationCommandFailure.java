package com.forkdevs.driveos.platform.iot.application.commandservices;

/**
 * Interface representing possible failure outcomes of OBD2 Device Registration command operations.
 */
public sealed interface Obd2DeviceRegistrationCommandFailure permits
        Obd2DeviceRegistrationCommandFailure.NotFound,
        Obd2DeviceRegistrationCommandFailure.InvalidState {
    String message();
    record NotFound(String message) implements Obd2DeviceRegistrationCommandFailure {}
    record InvalidState(String message) implements Obd2DeviceRegistrationCommandFailure {}
}
