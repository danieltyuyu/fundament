package com.forkdevs.driveos.platform.fleet.domain.model.valueobjects;

/**
 * Value object representing the status of a Customer Registration.
 */
public record CustomerRegistrationStatus(String value) {
    public CustomerRegistrationStatus {
        if (value == null || value.isBlank()) {
            throw new IllegalArgumentException("Customer registration status cannot be null or empty");
        }
    }

    public static final CustomerRegistrationStatus ACTIVE = new CustomerRegistrationStatus("ACTIVE");
    public static final CustomerRegistrationStatus INACTIVE = new CustomerRegistrationStatus("INACTIVE");
}

