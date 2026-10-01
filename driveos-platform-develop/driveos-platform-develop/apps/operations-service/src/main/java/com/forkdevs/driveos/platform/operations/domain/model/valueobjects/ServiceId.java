package com.forkdevs.driveos.platform.operations.domain.model.valueobjects;

import java.util.UUID;

/**
 * Value Object representing a Service's unique identifier.
 * Encapsulates a UUID and ensures it is not null.
 * @param value The UUID value representing the Service ID.
 * @author Joel Huamani Estefanero
 */
public record ServiceId(UUID value) {

    private static final String NOT_NULL_UUID_REGEX = "operations.error.serviceId.required";

    /**
     * Constructor that validates the ServiceId value.
     * @param value The UUID value to be encapsulated by this ServiceId.
     * @throws IllegalArgumentException if the value is null, with a message defined by NOT
     */
    public ServiceId {
        if (value == null) {
            throw new IllegalArgumentException(NOT_NULL_UUID_REGEX);
        }
    }
}
