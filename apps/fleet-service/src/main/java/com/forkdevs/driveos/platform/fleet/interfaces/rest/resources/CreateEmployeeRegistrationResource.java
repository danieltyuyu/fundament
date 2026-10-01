package com.forkdevs.driveos.platform.fleet.interfaces.rest.resources;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.math.BigDecimal;
import java.util.UUID;

public record CreateEmployeeRegistrationResource(
        @NotNull(message = "fleet.error.resource.employeeId.required") UUID employeeId,
        @NotNull(message = "fleet.error.resource.branchId.required") UUID branchId,
        @NotBlank(message = "fleet.error.resource.speciality.required") @Size(max = 50, message = "fleet.error.resource.speciality.size") String speciality,
        @Size(max = 50, message = "fleet.error.resource.specialityName.size") String specialityName,
        @NotNull(message = "fleet.error.resource.salary.required") BigDecimal salary
) {
}
