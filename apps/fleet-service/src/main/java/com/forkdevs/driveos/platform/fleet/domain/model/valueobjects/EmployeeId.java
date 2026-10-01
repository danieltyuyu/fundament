package com.forkdevs.driveos.platform.fleet.domain.model.valueobjects;

import java.util.UUID;

public record EmployeeId(UUID value) {
    public EmployeeId {
        if (value == null) {
            throw new IllegalArgumentException("fleet.error.employeeId.required");
        }
    }
}
