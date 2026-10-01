package com.forkdevs.driveos.platform.operations.interfaces.rest.resources;

import java.util.UUID;

public record ServiceResource(
        UUID id,
        UUID branchId,
        String name,
        Double price
) {
}
