package com.forkdevs.driveos.platform.iot.domain.model.valueobjects;

import java.util.UUID;

/**
 * Value object representing the unique identifier of a Telemetry Snapshot in the system.
 * @param value The UUID value representing the Telemetry Snapshot ID. It must not be null.
 */
public record TelemetrySnapshotId(UUID value) {

    private static final String NOT_NULL_UUID_REGEX = "iot.error.telemetrySnapshotId.required";

    /**
     * Constructs a TelemetrySnapshotId value object with validation to ensure the UUID is not null.
     * @param value the UUID value representing the Telemetry Snapshot ID
     * @throws IllegalArgumentException if the UUID value is null
     */
    public TelemetrySnapshotId {
        if (value == null) {
            throw new IllegalArgumentException(NOT_NULL_UUID_REGEX);
        }
    }

    /**
     * Static factory method to generate a new TelemetrySnapshotId with a random UUID.
     * @return a new TelemetrySnapshotId with a random UUID
     */
    public static TelemetrySnapshotId random() {
        return new TelemetrySnapshotId(UUID.randomUUID());
    }
}
