package com.forkdevs.driveos.platform.core.interfaces.rest.resources;

import java.util.UUID;

public record CreateBranchResource(
        UUID workshopId,
        String code,
        String name,
        String address,
        String phone
) {
}
