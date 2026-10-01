package com.forkdevs.driveos.platform.operations.domain.model.valueobjects;

import java.util.UUID;

/**
 * Value Object representing a Product's unique identifier.
 * @param value The UUID value representing the Product ID. It must not be null.
 * @author Joel Huamani Estefanero
 */
public record ProductId(UUID value) {

    private static final String NOT_NULL_UUID_REGEX = "operations.error.productId.required";

    /**
     * Constructor for ProductId that validates the input value to ensure it is not null. If the value is null, an IllegalArgumentException is thrown with a specific error message.
     * @param value the UUID value representing the Product ID
     * @throws IllegalArgumentException if the value is null
     */
    public ProductId {
        if (value == null) {
            throw new IllegalArgumentException(NOT_NULL_UUID_REGEX);
        }
    }
}
