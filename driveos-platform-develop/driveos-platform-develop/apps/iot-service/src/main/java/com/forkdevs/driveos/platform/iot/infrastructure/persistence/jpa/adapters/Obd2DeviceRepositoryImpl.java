package com.forkdevs.driveos.platform.iot.infrastructure.persistence.jpa.adapters;

import com.forkdevs.driveos.platform.iot.domain.model.aggregates.Obd2Device;
import com.forkdevs.driveos.platform.iot.domain.model.valueobjects.Obd2DeviceId;
import com.forkdevs.driveos.platform.iot.domain.repositories.Obd2DeviceRepository;
import com.forkdevs.driveos.platform.iot.infrastructure.persistence.jpa.assemblers.Obd2DevicePersistenceAssembler;
import com.forkdevs.driveos.platform.iot.infrastructure.persistence.jpa.entities.Obd2DevicePersistenceEntity;
import com.forkdevs.driveos.platform.iot.infrastructure.persistence.jpa.repositories.Obd2DevicePersistenceRepository;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Repository;

import com.forkdevs.driveos.platform.iot.domain.model.valueobjects.Obd2DeviceStatus;
import com.forkdevs.driveos.platform.shared.domain.model.valueobjects.BranchId;
import java.util.List;
import java.util.Optional;

@Repository
public class Obd2DeviceRepositoryImpl implements Obd2DeviceRepository {

    private final Obd2DevicePersistenceRepository persistenceRepository;
    private final ApplicationEventPublisher eventPublisher;

    public Obd2DeviceRepositoryImpl(
            Obd2DevicePersistenceRepository persistenceRepository,
            ApplicationEventPublisher eventPublisher
    ) {
        this.persistenceRepository = persistenceRepository;
        this.eventPublisher = eventPublisher;
    }

    @Override
    public Obd2Device save(Obd2Device obd2Device) {
        Obd2DevicePersistenceEntity entity;
        if (obd2Device.getId() != null) {
            entity = persistenceRepository.findById(obd2Device.getId().value())
                    .orElseGet(Obd2DevicePersistenceEntity::new);
        } else {
            entity = new Obd2DevicePersistenceEntity();
        }

        entity.setId(obd2Device.getId() != null ? obd2Device.getId().value() : null);
        entity.setBranchId(obd2Device.getBranchId());
        entity.setMacAddress(obd2Device.getMacAddress());
        entity.setLastPing(obd2Device.getLastPing());
        entity.setStatus(obd2Device.getStatus() != null ? obd2Device.getStatus().value() : null);
        entity.setVersion(obd2Device.getVersion());

        Obd2DevicePersistenceEntity savedEntity = persistenceRepository.save(entity);
        Obd2Device savedDevice = Obd2DevicePersistenceAssembler.toDomainEntity(savedEntity);

        // Publish events if any
        if (savedDevice != null) {
            obd2Device.domainEvents().forEach(eventPublisher::publishEvent);
            obd2Device.clearDomainEvents();
        }
        return savedDevice;
    }

    @Override
    public Optional<Obd2Device> findById(Obd2DeviceId id) {
        return persistenceRepository.findById(id.value())
                .map(Obd2DevicePersistenceAssembler::toDomainEntity);
    }

    @Override
    public Optional<Obd2Device> findByMacAddress(String macAddress) {
        return persistenceRepository.findByMacAddress(macAddress)
                .map(Obd2DevicePersistenceAssembler::toDomainEntity);
    }

    @Override
    public boolean existsByMacAddress(String macAddress) {
        return persistenceRepository.existsByMacAddress(macAddress);
    }

    @Override
    public void delete(Obd2DeviceId id) {
        persistenceRepository.deleteById(id.value());
    }

    @Override
    public List<Obd2Device> findAllByBranchId(BranchId branchId) {
        return persistenceRepository.findAllByBranchId(branchId).stream()
                .map(Obd2DevicePersistenceAssembler::toDomainEntity)
                .toList();
    }

    @Override
    public List<Obd2Device> findAllByBranchIdAndStatus(BranchId branchId, Obd2DeviceStatus status) {
        return persistenceRepository.findAllByBranchIdAndStatus(branchId, status.value()).stream()
                .map(Obd2DevicePersistenceAssembler::toDomainEntity)
                .toList();
    }
}
