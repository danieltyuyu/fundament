package com.forkdevs.driveos.platform.operations.domain.model.valueobjects;

import java.util.UUID;

/**
 * Value Object representing a Work Order Task's unique identifier.
 * Encapsulates a UUID and ensures it is not null.
 * @param value The UUID value representing the Work Order Task ID.
 * @author Joel Huamani Estefanero
 */
public record WorkOrderTaskId(UUID value) {

    private static final String NOT_NULL_UUID_REGEX = "operations.error.workOrderTaskId.required";

    /**
     * Constructor that validates the WorkOrderTaskId value.
     * @param value The UUID value to be encapsulated by this WorkOrderTaskId.
     * @throws IllegalArgumentException if the value is null, with a message defined by NOT_NULL_UUID_REGEX.
     */
    public WorkOrderTaskId {
        if (value == null) {
            throw new IllegalArgumentException(NOT_NULL_UUID_REGEX);
        }
    }
}
