package com.forkdevs.driveos.platform.core.interfaces.rest.resources;

import java.util.UUID;

public record CustomerResource(
        UUID id,
        UUID userId,
        boolean isCorporate,
        String firstName,
        String lastName,
        String businessName,
        String documentType,
        String documentNumber,
        String phone
) {
}
