package com.forkdevs.driveos.platform.core.interfaces.rest.transform;

import com.forkdevs.driveos.platform.core.domain.model.aggregates.Branch;
import com.forkdevs.driveos.platform.core.interfaces.rest.resources.BranchResource;

public class BranchResourceFromEntityAssembler {
    public static BranchResource toResourceFromEntity(Branch entity) {
        return new BranchResource(
                entity.getId() != null ? entity.getId().value() : null,
                entity.getWorkshopId() != null ? entity.getWorkshopId().value() : null,
                entity.getCode(),
                entity.getName(),
                entity.getAddress() != null ? entity.getAddress().value() : null,
                entity.getPhone() != null ? entity.getPhone().value() : null
        );
    }
}
