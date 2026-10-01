package com.forkdevs.driveos.platform.iot.domain.model.valueobjects;

/**
 * Value object representing the status of an OBD2 Device Registration.
 */
public record Obd2RegistrationStatus(String value) {
    public Obd2RegistrationStatus {
        if (value == null || value.isBlank()) {
            throw new IllegalArgumentException("Obd2 registration status cannot be null or empty");
        }
    }

    public static final Obd2RegistrationStatus ACTIVE = new Obd2RegistrationStatus("ACTIVE");
    public static final Obd2RegistrationStatus INACTIVE = new Obd2RegistrationStatus("INACTIVE");
}
