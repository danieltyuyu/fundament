package com.forkdevs.driveos.platform.iot.domain.model.queries;

import com.forkdevs.driveos.platform.iot.domain.model.valueobjects.Obd2DeviceId;

/**
 * Query to retrieve the complete history of telemetry snapshots for a specific OBD2 device.
 */
public record GetTelemetrySnapshotHistoryQuery(Obd2DeviceId obd2DeviceId) {
}
