package com.forkdevs.driveos.platform.iot.domain.model.valueobjects;

import java.util.UUID;

/**
 * Value object representing the unique identifier of an OBD2 Device in the system.
 * @param value The UUID value representing the OBD2 Device ID. It must not be null.
 */
public record Obd2DeviceId(UUID value) {

    private static final String NOT_NULL_UUID_REGEX = "iot.error.obd2DeviceId.required";

    /**
     * Constructs an Obd2DeviceId value object with validation to ensure the UUID is not null.
     * @param value the UUID value representing the OBD2 Device ID
     * @throws IllegalArgumentException if the UUID value is null
     */
    public Obd2DeviceId {
        if (value == null) {
            throw new IllegalArgumentException(NOT_NULL_UUID_REGEX);
        }
    }

    /**
     * Static factory method to generate a new Obd2DeviceId with a random UUID.
     * @return a new Obd2DeviceId with a random UUID
     */
    public static Obd2DeviceId random() {
        return new Obd2DeviceId(UUID.randomUUID());
    }
}
