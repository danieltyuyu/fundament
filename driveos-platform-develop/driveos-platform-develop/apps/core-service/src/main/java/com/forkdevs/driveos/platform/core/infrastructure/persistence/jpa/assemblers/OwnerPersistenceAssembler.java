package com.forkdevs.driveos.platform.core.infrastructure.persistence.jpa.assemblers;

import com.forkdevs.driveos.platform.core.domain.model.aggregates.Owner;
import com.forkdevs.driveos.platform.core.domain.model.valueobjects.Document;
import com.forkdevs.driveos.platform.core.domain.model.valueobjects.OwnerId;
import com.forkdevs.driveos.platform.core.domain.model.valueobjects.PersonName;
import com.forkdevs.driveos.platform.core.domain.model.valueobjects.Phone;
import com.forkdevs.driveos.platform.core.domain.model.valueobjects.UserId;
import com.forkdevs.driveos.platform.core.infrastructure.persistence.jpa.entities.OwnerPersistenceEntity;

public final class OwnerPersistenceAssembler {

    public OwnerPersistenceAssembler() {}

    public static OwnerPersistenceEntity toEntity(Owner owner, OwnerPersistenceEntity entity) {
        if (entity == null) {
            entity = new OwnerPersistenceEntity();
        }
        if (owner.getVersion() != null) {
            entity.setId(owner.getId() != null ? owner.getId().value() : null);
        }
        entity.setUserId(owner.getUserId() != null ? owner.getUserId().value() : null);
        
        if (owner.getName() != null) {
            entity.setFirstName(owner.getName().firstName());
            entity.setLastName(owner.getName().lastName());
        }
        
        if (owner.getDocument() != null) {
            entity.setDocumentType(owner.getDocument().getDocumentType().name());
            entity.setDocumentNumber(owner.getDocument().getDocumentNumber());
        }
        
        entity.setPhone(owner.getPhone() != null ? owner.getPhone().value() : null);
        entity.setCreatedAt(owner.getCreatedAt());
        entity.setUpdatedAt(owner.getUpdatedAt());
        entity.setDeletedAt(owner.getDeletedAt());
        entity.setVersion(owner.getVersion());
        return entity;
    }

    public static Owner toDomain(OwnerPersistenceEntity entity) {
        PersonName personName = new PersonName(entity.getFirstName(), entity.getLastName());
        Document document = new Document(entity.getDocumentType(), entity.getDocumentNumber());

        return new Owner(
                new OwnerId(entity.getId()),
                new UserId(entity.getUserId()),
                personName,
                document,
                new Phone(entity.getPhone()),
                entity.getCreatedAt(),
                entity.getUpdatedAt(),
                entity.getDeletedAt(),
                entity.getVersion()
        );
    }
}
