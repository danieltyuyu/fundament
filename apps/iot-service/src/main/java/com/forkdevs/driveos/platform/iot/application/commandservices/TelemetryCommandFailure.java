package com.forkdevs.driveos.platform.iot.application.commandservices;

/**
 * Interface representing possible failure outcomes of Telemetry command operations.
 */
public sealed interface TelemetryCommandFailure permits
        TelemetryCommandFailure.NotFound,
        TelemetryCommandFailure.InvalidState {
    String message();
    record NotFound(String message) implements TelemetryCommandFailure {}
    record InvalidState(String message) implements TelemetryCommandFailure {}
}
