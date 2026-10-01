package com.forkdevs.driveos.platform.fleet.interfaces.rest.resources;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;

public record UpdateEmployeeRegistrationResource(
        @NotBlank String speciality,
        @NotBlank String specialityName,
        @NotNull @Min(0) BigDecimal salary
) {
}
