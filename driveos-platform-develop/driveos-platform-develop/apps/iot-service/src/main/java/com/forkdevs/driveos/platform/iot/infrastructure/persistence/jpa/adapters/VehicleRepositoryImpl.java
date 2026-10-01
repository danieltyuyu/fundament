package com.forkdevs.driveos.platform.iot.infrastructure.persistence.jpa.adapters;

import com.forkdevs.driveos.platform.iot.domain.model.aggregates.Vehicle;
import com.forkdevs.driveos.platform.iot.domain.repositories.VehicleRepository;
import com.forkdevs.driveos.platform.iot.infrastructure.persistence.jpa.assemblers.VehiclePersistenceAssembler;
import com.forkdevs.driveos.platform.iot.infrastructure.persistence.jpa.entities.VehiclePersistenceEntity;
import com.forkdevs.driveos.platform.iot.infrastructure.persistence.jpa.repositories.VehiclePersistenceRepository;
import com.forkdevs.driveos.platform.shared.domain.model.valueobjects.BranchId;
import com.forkdevs.driveos.platform.shared.domain.model.valueobjects.VehicleId;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * JPA adapter implementing the VehicleRepository port.
 */
@Repository
public class VehicleRepositoryImpl implements VehicleRepository {

    private final VehiclePersistenceRepository persistenceRepository;

    public VehicleRepositoryImpl(VehiclePersistenceRepository persistenceRepository) {
        this.persistenceRepository = persistenceRepository;
    }

    @Override
    public Optional<Vehicle> findById(VehicleId id) {
        return persistenceRepository.findById(id.value())
                .map(VehiclePersistenceAssembler::toDomainEntity);
    }

    @Override
    public List<Vehicle> findAvailableForLinkingByBranchId(BranchId branchId) {
        return persistenceRepository.findAvailableForLinkingByBranchId(branchId.value()).stream()
                .map(VehiclePersistenceAssembler::toDomainEntity)
                .toList();
    }

    @Override
    public Vehicle save(Vehicle vehicle) {
        VehiclePersistenceEntity entity;
        if (vehicle.getId() != null) {
            entity = persistenceRepository.findById(vehicle.getId().value())
                    .orElseGet(VehiclePersistenceEntity::new);
        } else {
            entity = new VehiclePersistenceEntity();
        }

        entity.setId(vehicle.getId() != null ? vehicle.getId().value() : null);
        entity.setPlateNumber(vehicle.getPlateNumber());
        entity.setBrand(vehicle.getBrand());
        entity.setModel(vehicle.getModel());
        entity.setYear(vehicle.getYear());
        entity.setVin(vehicle.getVin());
        entity.setDeletedAt(vehicle.getDeletedAt());
        entity.setVersion(vehicle.getVersion());

        VehiclePersistenceEntity savedEntity = persistenceRepository.save(entity);
        return VehiclePersistenceAssembler.toDomainEntity(savedEntity);
    }

    @Override
    public Optional<Vehicle> findByVin(String vin) {
        return persistenceRepository.findByVin(vin)
                .map(VehiclePersistenceAssembler::toDomainEntity);
    }

    @Override
    public Optional<Vehicle> findByPlateNumber(String plateNumber) {
        return persistenceRepository.findByPlateNumber(plateNumber)
                .map(VehiclePersistenceAssembler::toDomainEntity);
    }

    @Override
    public void delete(VehicleId id) {
        persistenceRepository.deleteById(id.value());
    }
}
