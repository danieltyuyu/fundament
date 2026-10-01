package com.forkdevs.driveos.platform.iot.application.commandservices;

/**
 * Interface representing possible failure outcomes of Vehicle command operations.
 */
public sealed interface VehicleCommandFailure permits
        VehicleCommandFailure.NotFound,
        VehicleCommandFailure.InvalidState,
        VehicleCommandFailure.Duplicate {
    String message();
    record NotFound(String message) implements VehicleCommandFailure {}
    record InvalidState(String message) implements VehicleCommandFailure {}
    record Duplicate(String message) implements VehicleCommandFailure {}
}
