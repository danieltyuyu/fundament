package com.forkdevs.driveos.platform.iot.domain.model.commands;

import com.forkdevs.driveos.platform.iot.domain.model.valueobjects.Obd2DeviceId;

import java.time.Instant;
import java.util.List;

/**
 * Command to ingest a batch of telemetry snapshots from an OBD2 device.
 */
public record IngestTelemetryBatchCommand(
        Obd2DeviceId obd2DeviceId,
        List<TelemetrySnapshotData> snapshots
) {
    public record TelemetrySnapshotData(
            Integer rpm,
            Integer temperature,
            Double speedKmh,
            Integer odometerKm,
            Double fuelLevelPercent,
            Instant createdAt
    ) {}
}
