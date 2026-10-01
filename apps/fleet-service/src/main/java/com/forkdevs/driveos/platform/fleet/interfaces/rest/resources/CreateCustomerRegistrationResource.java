package com.forkdevs.driveos.platform.fleet.interfaces.rest.resources;

import jakarta.validation.constraints.NotNull;

import java.util.UUID;

public record CreateCustomerRegistrationResource(
        @NotNull(message = "fleet.error.resource.customerId.required") UUID customerId,
        @NotNull(message = "fleet.error.resource.branchId.required") UUID branchId
) {
}

