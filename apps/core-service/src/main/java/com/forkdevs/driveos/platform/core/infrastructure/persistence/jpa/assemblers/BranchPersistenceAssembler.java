package com.forkdevs.driveos.platform.core.infrastructure.persistence.jpa.assemblers;

import com.forkdevs.driveos.platform.core.domain.model.aggregates.Branch;
import com.forkdevs.driveos.platform.shared.domain.model.valueobjects.BranchId;
import com.forkdevs.driveos.platform.core.domain.model.valueobjects.Phone;
import com.forkdevs.driveos.platform.core.domain.model.valueobjects.WorkshopId;
import com.forkdevs.driveos.platform.core.infrastructure.persistence.jpa.entities.BranchPersistenceEntity;
import com.forkdevs.driveos.platform.shared.domain.model.valueobjects.Address;

public final class BranchPersistenceAssembler {

    private BranchPersistenceAssembler() {}

    public static BranchPersistenceEntity toEntity(Branch branch, BranchPersistenceEntity entity) {
        if (entity == null) {
            entity = new BranchPersistenceEntity();
        }
        if (branch.getVersion() != null) {
            entity.setId(branch.getId() != null ? branch.getId().value() : null);
        }
        entity.setWorkshopId(branch.getWorkshopId() != null ? branch.getWorkshopId().value() : null);
        entity.setCode(branch.getCode());
        entity.setName(branch.getName());
        if (branch.getAddress() != null) {
            entity.setAddress(branch.getAddress().value());
        }
        entity.setPhone(branch.getPhone() != null ? branch.getPhone().value() : null);
        entity.setCreatedAt(branch.getCreatedAt());
        entity.setUpdatedAt(branch.getUpdatedAt());
        entity.setDeletedAt(branch.getDeletedAt());
        entity.setCreatedBy(branch.getCreatedBy());
        entity.setUpdatedBy(branch.getUpdatedBy());
        entity.setVersion(branch.getVersion());
        return entity;
    }

    public static Branch toDomain(BranchPersistenceEntity entity) {
        return new Branch(
                new BranchId(entity.getId()),
                new WorkshopId(entity.getWorkshopId()),
                entity.getCode(),
                entity.getName(),
                new Address(entity.getAddress()),
                new Phone(entity.getPhone()),
                entity.getCreatedAt(),
                entity.getUpdatedAt(),
                entity.getDeletedAt(),
                entity.getCreatedBy(),
                entity.getUpdatedBy(),
                entity.getVersion()
        );
    }
}

