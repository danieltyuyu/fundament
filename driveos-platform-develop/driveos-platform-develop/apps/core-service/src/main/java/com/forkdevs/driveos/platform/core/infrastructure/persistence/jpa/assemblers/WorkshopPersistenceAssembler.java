package com.forkdevs.driveos.platform.core.infrastructure.persistence.jpa.assemblers;

import com.forkdevs.driveos.platform.core.domain.model.aggregates.Workshop;
import com.forkdevs.driveos.platform.core.domain.model.valueobjects.OwnerId;
import com.forkdevs.driveos.platform.core.domain.model.valueobjects.TaxId;
import com.forkdevs.driveos.platform.core.domain.model.valueobjects.WorkshopId;
import com.forkdevs.driveos.platform.core.infrastructure.persistence.jpa.entities.WorkshopPersistenceEntity;

public final class WorkshopPersistenceAssembler {

    public WorkshopPersistenceAssembler() {}

    public static WorkshopPersistenceEntity toEntity(Workshop workshop, WorkshopPersistenceEntity entity) {
        if (entity == null) {
            entity = new WorkshopPersistenceEntity();
        }
        if (workshop.getVersion() != null) {
            entity.setId(workshop.getId() != null ? workshop.getId().value() : null);
        }
        entity.setOwnerId(workshop.getOwnerId() != null ? workshop.getOwnerId().value() : null);
        entity.setBusinessName(workshop.getBusinessName());
        entity.setBrandName(workshop.getBrandName());
        entity.setTaxId(workshop.getTaxId() != null ? workshop.getTaxId().value() : null);
        entity.setMileageIntervalConfig(workshop.getMileageIntervalConfig());
        entity.setCreatedAt(workshop.getCreatedAt());
        entity.setUpdatedAt(workshop.getUpdatedAt());
        entity.setDeletedAt(workshop.getDeletedAt());
        entity.setVersion(workshop.getVersion());
        return entity;
    }

    public static Workshop toDomain(WorkshopPersistenceEntity entity) {
        return new Workshop(
                new WorkshopId(entity.getId()),
                new OwnerId(entity.getOwnerId()),
                entity.getBusinessName(),
                entity.getBrandName(),
                new TaxId(entity.getTaxId()),
                entity.getMileageIntervalConfig(),
                entity.getCreatedAt(),
                entity.getUpdatedAt(),
                entity.getDeletedAt(),
                entity.getVersion()
        );
    }
}
