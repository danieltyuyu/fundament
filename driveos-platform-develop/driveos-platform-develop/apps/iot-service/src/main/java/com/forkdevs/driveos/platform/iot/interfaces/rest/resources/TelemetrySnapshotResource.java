package com.forkdevs.driveos.platform.iot.interfaces.rest.resources;

import java.time.Instant;
import java.util.UUID;

/**
 * Resource representing a telemetry snapshot returned by query endpoints.
 */
public record TelemetrySnapshotResource(
        UUID id,
        UUID obd2DeviceRegistrationId,
        UUID branchId,
        Integer rpm,
        Integer temperature,
        Double speedKmh,
        Integer odometerKm,
        Double fuelLevelPercent,
        Instant createdAt
) {
}
