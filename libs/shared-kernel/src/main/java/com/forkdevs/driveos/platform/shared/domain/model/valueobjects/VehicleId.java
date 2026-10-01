package com.forkdevs.driveos.platform.shared.domain.model.valueobjects;

import java.util.UUID;

/**
 * Value object representing the unique identifier of a Vehicle in the system. This class encapsulates the UUID value and provides validation to ensure that it is not null.
 * @param value The UUID value representing the Vehicle ID. It must not be null.
 * @author Joel Huamani Estefanero
 */
public record VehicleId(UUID value) {

    private static final String NOT_NULL_UUID_REGEX = "shared.error.vehicleId.required";

    /**
     * Constructs a VehicleId value object with validation to ensure the UUID is not null.
     * @param value the UUID value representing the Vehicle ID
     * @throws IllegalArgumentException if the UUID value is null
     */
    public VehicleId {
        if (value == null) {
            throw new IllegalArgumentException(NOT_NULL_UUID_REGEX);
        }
    }
}
