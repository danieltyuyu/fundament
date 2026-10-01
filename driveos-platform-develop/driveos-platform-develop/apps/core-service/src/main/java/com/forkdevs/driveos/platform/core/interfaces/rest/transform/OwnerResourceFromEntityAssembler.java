package com.forkdevs.driveos.platform.core.interfaces.rest.transform;

import com.forkdevs.driveos.platform.core.domain.model.aggregates.Owner;
import com.forkdevs.driveos.platform.core.interfaces.rest.resources.OwnerResource;

public class OwnerResourceFromEntityAssembler {
    public static OwnerResource toResourceFromEntity(Owner entity) {
        return new OwnerResource(
                entity.getId() != null ? entity.getId().value() : null,
                entity.getUserId() != null ? entity.getUserId().value() : null,
                entity.getName() != null ? entity.getName().firstName() : null,
                entity.getName() != null ? entity.getName().lastName() : null,
                entity.getDocument() != null ? entity.getDocument().getDocumentType().name() : null,
                entity.getDocument() != null ? entity.getDocument().getDocumentNumber() : null,
                entity.getPhone() != null ? entity.getPhone().value() : null
        );
    }
}
