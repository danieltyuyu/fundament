package com.forkdevs.driveos.platform.fleet.interfaces.rest.resources;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

public record EmployeeRegistrationResource(
        UUID id,
        UUID employeeId,
        UUID branchId,
        String speciality,
        String specialityName,
        BigDecimal salary,
        String status,
        Instant createdAt,
        Instant updatedAt,
        Instant deletedAt) {
}