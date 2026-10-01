package com.forkdevs.driveos.platform.operations.domain.model.valueobjects;

import java.util.UUID;

/**
 * Value Object representing an Appointment's unique identifier.
 * @param value
 * @author Joel Huamani Estefanero
 */
public record AppointmentId(UUID value) {

    private static final String NOT_NULL_UUID_REGEX = "operations.error.appointmentId.required";

    /**
     * Constructor for AppointmentId that validates the input value to ensure it is not null. If the value is null, an IllegalArgumentException is thrown with a specific error message.
     * @param value the UUID value representing the Appointment ID
     * @throws IllegalArgumentException if the value is null
     */
    public AppointmentId {
        if (value == null) {
            throw new IllegalArgumentException(NOT_NULL_UUID_REGEX);
        }
    }
}
