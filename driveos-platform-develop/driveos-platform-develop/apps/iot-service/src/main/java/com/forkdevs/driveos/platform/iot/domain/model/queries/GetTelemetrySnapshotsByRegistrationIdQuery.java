package com.forkdevs.driveos.platform.iot.domain.model.queries;

import com.forkdevs.driveos.platform.iot.domain.model.valueobjects.Obd2DeviceRegistrationId;

/**
 * Query representing the request to retrieve all telemetry snapshots for a specific OBD2 device registration.
 */
public record GetTelemetrySnapshotsByRegistrationIdQuery(
        Obd2DeviceRegistrationId obd2DeviceRegistrationId
) {
    public GetTelemetrySnapshotsByRegistrationIdQuery {
        if (obd2DeviceRegistrationId == null) {
            throw new IllegalArgumentException("obd2DeviceRegistrationId cannot be null");
        }
    }
}
