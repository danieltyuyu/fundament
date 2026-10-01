package com.forkdevs.driveos.platform.core.interfaces.rest.resources;

import java.util.UUID;

public record WorkshopResource(
        UUID id,
        UUID ownerId,
        String businessName,
        String brandName,
        String taxId,
        int mileageIntervalConfig
) {
}
