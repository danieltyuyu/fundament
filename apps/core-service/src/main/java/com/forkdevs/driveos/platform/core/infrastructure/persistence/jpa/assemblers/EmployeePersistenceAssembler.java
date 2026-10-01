package com.forkdevs.driveos.platform.core.infrastructure.persistence.jpa.assemblers;

import com.forkdevs.driveos.platform.core.domain.model.aggregates.Employee;
import com.forkdevs.driveos.platform.core.domain.model.valueobjects.Document;
import com.forkdevs.driveos.platform.core.domain.model.valueobjects.EmployeeId;
import com.forkdevs.driveos.platform.core.domain.model.valueobjects.PersonName;
import com.forkdevs.driveos.platform.core.domain.model.valueobjects.Phone;
import com.forkdevs.driveos.platform.core.domain.model.valueobjects.UserId;
import com.forkdevs.driveos.platform.core.infrastructure.persistence.jpa.entities.EmployeePersistenceEntity;

public final class EmployeePersistenceAssembler {

    public EmployeePersistenceAssembler() {}

    public static EmployeePersistenceEntity toEntity(Employee employee, EmployeePersistenceEntity entity) {
        if (entity == null) {
            entity = new EmployeePersistenceEntity();
        }
        if (employee.getVersion() != null) {
            entity.setId(employee.getId() != null ? employee.getId().value() : null);
        }
        entity.setUserId(employee.getUserId() != null ? employee.getUserId().value() : null);
        
        if (employee.getName() != null) {
            entity.setFirstName(employee.getName().firstName());
            entity.setLastName(employee.getName().lastName());
        }
        
        if (employee.getDocument() != null) {
            entity.setDocumentType(employee.getDocument().getDocumentType().name());
            entity.setDocumentNumber(employee.getDocument().getDocumentNumber());
        }
        
        entity.setPhone(employee.getPhone() != null ? employee.getPhone().value() : null);
        entity.setCreatedAt(employee.getCreatedAt());
        entity.setUpdatedAt(employee.getUpdatedAt());
        entity.setDeletedAt(employee.getDeletedAt());
        entity.setVersion(employee.getVersion());
        return entity;
    }

    public static Employee toDomain(EmployeePersistenceEntity entity) {
        PersonName personName = new PersonName(entity.getFirstName(), entity.getLastName());
        Document document = new Document(entity.getDocumentType(), entity.getDocumentNumber());

        return new Employee(
                new EmployeeId(entity.getId()),
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
