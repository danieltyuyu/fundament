package com.forkdevs.driveos.platform.core.interfaces.rest.transform;

import com.forkdevs.driveos.platform.core.domain.model.aggregates.Workshop;
import com.forkdevs.driveos.platform.core.interfaces.rest.resources.WorkshopResource;

public class WorkshopResourceFromEntityAssembler {
    public static WorkshopResource toResourceFromEntity(Workshop entity) {
        return new WorkshopResource(
                entity.getId() != null ? entity.getId().value() : null,
                entity.getOwnerId() != null ? entity.getOwnerId().value() : null,
                entity.getBusinessName(),
                entity.getBrandName(),
                entity.getTaxId() != null ? entity.getTaxId().value() : null,
                entity.getMileageIntervalConfig()
        );
    }
}
