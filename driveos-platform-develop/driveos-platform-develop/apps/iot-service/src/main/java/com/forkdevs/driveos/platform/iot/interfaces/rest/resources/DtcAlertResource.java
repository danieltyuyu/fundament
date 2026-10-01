package com.forkdevs.driveos.platform.iot.interfaces.rest.resources;

import java.time.Instant;
import java.util.UUID;

/**
 * Resource representing a DTC Alert returned in REST responses.
 */
public record DtcAlertResource(
        UUID id,
        UUID telemetrySnapshotId,
        UUID branchId,
        String dtcCode,
        String description,
        String severity,
        Instant createdAt
) {
}
