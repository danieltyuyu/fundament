package com.forkdevs.driveos.platform.core.domain.model.valueobjects;

import java.util.UUID;

public record EmployeeId(UUID value) {
    public EmployeeId {
        if (value == null) {
            throw new IllegalArgumentException("core.error.employeeId.required");
        }
    }
}
