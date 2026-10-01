package com.forkdevs.driveos.platform.fleet.interfaces.rest.resources;

import jakarta.validation.constraints.NotNull;

public record UpdateCustomerRegistrationResource(
        @NotNull(message = "fleet.error.resource.status.required") String status
) {
}

