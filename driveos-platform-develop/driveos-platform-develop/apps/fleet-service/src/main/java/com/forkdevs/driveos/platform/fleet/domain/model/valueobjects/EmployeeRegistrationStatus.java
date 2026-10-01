package com.forkdevs.driveos.platform.fleet.domain.model.valueobjects;

public record EmployeeRegistrationStatus(String value) {
    public EmployeeRegistrationStatus {
        if (value == null || value.isBlank())
            throw new IllegalArgumentException("Employee registration status cannot be null or empty");
    }
    public static final EmployeeRegistrationStatus ACTIVE   = new EmployeeRegistrationStatus("ACTIVE");
    public static final EmployeeRegistrationStatus INACTIVE = new EmployeeRegistrationStatus("INACTIVE");
}
