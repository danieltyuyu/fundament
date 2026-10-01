package com.forkdevs.driveos.platform.iot.domain.model.valueobjects;

import java.util.UUID;

/**
 * Value object representing the unique identifier of an OBD2 Device Registration in the system.
 * @param value The UUID value representing the Registration ID. It must not be null.
 */
public record Obd2DeviceRegistrationId(UUID value) {

    private static final String NOT_NULL_UUID_REGEX = "iot.error.obd2DeviceRegistrationId.required";

    /**
     * Constructs an Obd2DeviceRegistrationId value object with validation to ensure the UUID is not null.
     * @param value the UUID value representing the Registration ID
     * @throws IllegalArgumentException if the UUID value is null
     */
    public Obd2DeviceRegistrationId {
        if (value == null) {
            throw new IllegalArgumentException(NOT_NULL_UUID_REGEX);
        }
    }

    /**
     * Static factory method to generate a new Obd2DeviceRegistrationId with a random UUID.
     * @return a new Obd2DeviceRegistrationId with a random UUID
     */
    public static Obd2DeviceRegistrationId random() {
        return new Obd2DeviceRegistrationId(UUID.randomUUID());
    }
}
