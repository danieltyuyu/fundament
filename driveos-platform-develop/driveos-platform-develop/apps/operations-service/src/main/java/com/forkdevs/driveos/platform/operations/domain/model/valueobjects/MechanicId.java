package com.forkdevs.driveos.platform.operations.domain.model.valueobjects;

import java.util.UUID;

/**
 * Value Object representing a Mechanic's unique identifier.
 * Encapsulates a UUID and ensures it is not null.
 * @param value The UUID value representing the Mechanic ID.
 * @author Joel Huamani Estefanero
 */
public record MechanicId(UUID value) {

    private static final String NOT_NULL_UUID_REGEX = "operations.error.mechanicId.required";

    /**
     * Constructor that validates the MechanicId value.
     * @param value The UUID value to be encapsulated by this MechanicId.
     * @throws IllegalArgumentException if the value is null, with a message defined by NOT_NULL_UUID_REGEX.
     */
    public MechanicId {
        if (value == null) {
            throw new IllegalArgumentException(NOT_NULL_UUID_REGEX);
        }
    }
}
