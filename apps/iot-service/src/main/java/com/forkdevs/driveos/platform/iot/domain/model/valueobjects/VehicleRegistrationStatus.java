package com.forkdevs.driveos.platform.iot.domain.model.valueobjects;

/**
 * Value object representing the status of a Vehicle Registration.
 */
public record VehicleRegistrationStatus(String value) {
    public VehicleRegistrationStatus {
        if (value == null || value.isBlank()) {
            throw new IllegalArgumentException("Vehicle registration status cannot be null or empty");
        }
    }

    public static final VehicleRegistrationStatus ACTIVE = new VehicleRegistrationStatus("ACTIVE");
    public static final VehicleRegistrationStatus PREVIOUS = new VehicleRegistrationStatus("PREVIOUS");
}
