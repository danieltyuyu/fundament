package com.forkdevs.driveos.platform.core.infrastructure.persistence.jpa.assemblers;

import com.forkdevs.driveos.platform.core.domain.model.aggregates.Customer;
import com.forkdevs.driveos.platform.shared.domain.model.valueobjects.CustomerId;
import com.forkdevs.driveos.platform.core.domain.model.valueobjects.Document;
import com.forkdevs.driveos.platform.core.domain.model.valueobjects.PersonName;
import com.forkdevs.driveos.platform.core.domain.model.valueobjects.Phone;
import com.forkdevs.driveos.platform.core.domain.model.valueobjects.UserId;
import com.forkdevs.driveos.platform.core.infrastructure.persistence.jpa.entities.CustomerPersistenceEntity;

public final class CustomerPersistenceAssembler {

    private CustomerPersistenceAssembler() {}

    public static CustomerPersistenceEntity toEntity(Customer customer, CustomerPersistenceEntity entity) {
        if (entity == null) {
            entity = new CustomerPersistenceEntity();
        }
        if (customer.getVersion() != null) {
            entity.setId(customer.getId() != null ? customer.getId().value() : null);
        }
        entity.setUserId(customer.getUserId() != null ? customer.getUserId().value() : null);
        entity.setCorporate(customer.isCorporate());
        
        if (customer.getName() != null) {
            entity.setFirstName(customer.getName().firstName());
            entity.setLastName(customer.getName().lastName());
        }
        
        entity.setBusinessName(customer.getBusinessName());
        
        if (customer.getDocument() != null) {
            entity.setDocumentType(customer.getDocument().getDocumentType().name());
            entity.setDocumentNumber(customer.getDocument().getDocumentNumber());
        }
        
        entity.setPhone(customer.getPhone() != null ? customer.getPhone().value() : null);
        entity.setCreatedAt(customer.getCreatedAt());
        entity.setUpdatedAt(customer.getUpdatedAt());
        entity.setDeletedAt(customer.getDeletedAt());
        entity.setVersion(customer.getVersion());
        return entity;
    }

    public static Customer toDomain(CustomerPersistenceEntity entity) {
        PersonName personName = null;
        if (entity.getFirstName() != null && entity.getLastName() != null) {
            personName = new PersonName(entity.getFirstName(), entity.getLastName());
        }
        
        Document document = null;
        if (entity.getDocumentType() != null && entity.getDocumentNumber() != null) {
            document = new Document(entity.getDocumentType(), entity.getDocumentNumber());
        }

        return new Customer(
                new CustomerId(entity.getId()),
                new UserId(entity.getUserId()),
                entity.isCorporate(),
                personName,
                entity.getBusinessName(),
                document,
                new Phone(entity.getPhone()),
                entity.getCreatedAt(),
                entity.getUpdatedAt(),
                entity.getDeletedAt(),
                entity.getVersion()
        );
    }
}

