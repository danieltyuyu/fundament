package com.forkdevs.driveos.platform.core.interfaces.rest.resources;

public record UpdateCustomerResource(
        String firstName,
        String lastName,
        String businessName,
        String documentType,
        String documentNumber,
        String phone
) {
}
