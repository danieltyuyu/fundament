package com.forkdevs.driveos.platform.shared.domain.model.valueobjects;

import java.util.UUID;

/**
 * Value Object representing a Branch ID in the system. It encapsulates a UUID value and ensures that it is not null.
 * @param value
 * @author Joel Huamani Estefanero
 */
public record BranchId(UUID value) {

    private static final String NOT_NULL_UUID_REGEX = "shared.error.branchId.required";

    /**
     * Constructor for BranchId that validates the input value to ensure it is not null. If the value is null, an IllegalArgumentException is thrown with a specific error message.
     * @param value the UUID value representing the Branch ID
     * @throws IllegalArgumentException if the value is null
     */
    public BranchId {
        if (value == null) {
            throw new IllegalArgumentException(NOT_NULL_UUID_REGEX);
        }
    }
}
