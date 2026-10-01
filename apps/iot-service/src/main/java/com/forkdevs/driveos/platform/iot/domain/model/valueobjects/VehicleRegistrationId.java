package com.forkdevs.driveos.platform.iot.domain.model.valueobjects;

import java.util.UUID;

/**
 * Value object representing the unique identifier of a Vehicle Registration in the system.
 * @param value The UUID value representing the Registration ID. It must not be null.
 */
public record VehicleRegistrationId(UUID value) {

    private static final String NOT_NULL_UUID_REGEX = "iot.error.vehicleRegistrationId.required";

    /**
     * Constructs a VehicleRegistrationId value object with validation to ensure the UUID is not null.
     * @param value the UUID value representing the Registration ID
     * @throws IllegalArgumentException if the UUID value is null
     */
    public VehicleRegistrationId {
        if (value == null) {
            throw new IllegalArgumentException(NOT_NULL_UUID_REGEX);
        }
    }

    /**
     * Static factory method to generate a new VehicleRegistrationId with a random UUID.
     * @return a new VehicleRegistrationId with a random UUID
     */
    public static VehicleRegistrationId random() {
        return new VehicleRegistrationId(UUID.randomUUID());
    }
}
