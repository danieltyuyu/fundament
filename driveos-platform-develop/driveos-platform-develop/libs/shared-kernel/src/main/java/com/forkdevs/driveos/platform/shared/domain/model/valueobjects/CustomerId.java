package com.forkdevs.driveos.platform.shared.domain.model.valueobjects;

import java.util.UUID;

/**
 * Value Object representing a Customer's unique identifier.
 * Encapsulates a UUID and ensures it is not null.
 * @param value The UUID value representing the Customer ID.
 * @author Joel Huamani Estefanero
 */
public record CustomerId(UUID value) {

    private static final String NOT_NULL_UUID_REGEX = "shared.error.customerId.required";

    /**
     * Constructor that validates the CustomerId value.
     * @param value The UUID value to be encapsulated by this CustomerId.
     * @throws IllegalArgumentException if the value is null, with a message defined by NOT_NULL_UUID_REGEX.
     */
    public CustomerId {
        if (value == null) {
            throw new IllegalArgumentException(NOT_NULL_UUID_REGEX);
        }
    }
}
