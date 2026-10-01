package com.forkdevs.driveos.platform.iot.infrastructure.persistence.jpa.assemblers;

import com.forkdevs.driveos.platform.iot.domain.model.aggregates.Vehicle;
import com.forkdevs.driveos.platform.iot.infrastructure.persistence.jpa.entities.VehiclePersistenceEntity;
import com.forkdevs.driveos.platform.shared.domain.model.valueobjects.VehicleId;

/**
 * Assembler class to transform between Vehicle persistence entities and domain aggregates.
 */
public class VehiclePersistenceAssembler {

    public static VehiclePersistenceEntity toPersistenceEntity(Vehicle domain) {
        if (domain == null) {
            return null;
        }
        VehiclePersistenceEntity entity = new VehiclePersistenceEntity();
        entity.setId(domain.getId() != null ? domain.getId().value() : null);
        entity.setPlateNumber(domain.getPlateNumber());
        entity.setBrand(domain.getBrand());
        entity.setModel(domain.getModel());
        entity.setYear(domain.getYear());
        entity.setVin(domain.getVin());
        entity.setCreatedAt(domain.getCreatedAt());
        entity.setUpdatedAt(domain.getUpdatedAt());
        entity.setDeletedAt(domain.getDeletedAt());
        entity.setVersion(domain.getVersion());
        return entity;
    }

    public static Vehicle toDomainEntity(VehiclePersistenceEntity entity) {
        if (entity == null) {
            return null;
        }
        return new Vehicle(
                new VehicleId(entity.getId()),
                entity.getPlateNumber(),
                entity.getBrand(),
                entity.getModel(),
                entity.getYear(),
                entity.getVin(),
                entity.getVersion(),
                entity.getCreatedAt(),
                entity.getUpdatedAt(),
                entity.getDeletedAt()
        );
    }
}
