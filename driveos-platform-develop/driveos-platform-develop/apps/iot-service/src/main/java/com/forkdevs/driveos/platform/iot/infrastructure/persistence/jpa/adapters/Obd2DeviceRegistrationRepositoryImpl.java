package com.forkdevs.driveos.platform.iot.infrastructure.persistence.jpa.adapters;

import com.forkdevs.driveos.platform.iot.domain.model.aggregates.Obd2DeviceRegistration;
import com.forkdevs.driveos.platform.iot.domain.model.valueobjects.Obd2DeviceId;
import com.forkdevs.driveos.platform.iot.domain.model.valueobjects.Obd2DeviceRegistrationId;
import com.forkdevs.driveos.platform.iot.domain.model.valueobjects.Obd2RegistrationStatus;
import com.forkdevs.driveos.platform.iot.domain.repositories.Obd2DeviceRegistrationRepository;
import com.forkdevs.driveos.platform.iot.infrastructure.persistence.jpa.assemblers.Obd2DeviceRegistrationPersistenceAssembler;
import com.forkdevs.driveos.platform.iot.infrastructure.persistence.jpa.entities.Obd2DeviceRegistrationPersistenceEntity;
import com.forkdevs.driveos.platform.iot.infrastructure.persistence.jpa.repositories.Obd2DeviceRegistrationPersistenceRepository;
import com.forkdevs.driveos.platform.shared.domain.model.valueobjects.BranchId;
import com.forkdevs.driveos.platform.shared.domain.model.valueobjects.VehicleId;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public class Obd2DeviceRegistrationRepositoryImpl implements Obd2DeviceRegistrationRepository {

    private final Obd2DeviceRegistrationPersistenceRepository persistenceRepository;
    private final ApplicationEventPublisher eventPublisher;

    public Obd2DeviceRegistrationRepositoryImpl(
            Obd2DeviceRegistrationPersistenceRepository persistenceRepository,
            ApplicationEventPublisher eventPublisher
    ) {
        this.persistenceRepository = persistenceRepository;
        this.eventPublisher = eventPublisher;
    }

    @Override
    public Obd2DeviceRegistration save(Obd2DeviceRegistration registration) {
        Obd2DeviceRegistrationPersistenceEntity entity = Obd2DeviceRegistrationPersistenceAssembler.toPersistenceEntity(registration);
        Obd2DeviceRegistrationPersistenceEntity savedEntity = persistenceRepository.save(entity);
        Obd2DeviceRegistration savedRegistration = Obd2DeviceRegistrationPersistenceAssembler.toDomainEntity(savedEntity);

        if (savedRegistration != null) {
            registration.domainEvents().forEach(eventPublisher::publishEvent);
            registration.clearDomainEvents();
        }
        return savedRegistration;
    }

    @Override
    public Optional<Obd2DeviceRegistration> findById(Obd2DeviceRegistrationId id) {
        return persistenceRepository.findById(id.value())
                .map(Obd2DeviceRegistrationPersistenceAssembler::toDomainEntity);
    }

    @Override
    public Optional<Obd2DeviceRegistration> findActiveByObd2DeviceId(Obd2DeviceId obd2DeviceId) {
        return persistenceRepository.findByObd2DeviceIdAndStatus(obd2DeviceId, Obd2RegistrationStatus.ACTIVE.value())
                .map(Obd2DeviceRegistrationPersistenceAssembler::toDomainEntity);
    }

    @Override
    public Optional<Obd2DeviceRegistration> findActiveByVehicleId(VehicleId vehicleId) {
        return persistenceRepository.findByVehicleIdAndStatus(vehicleId, Obd2RegistrationStatus.ACTIVE.value())
                .map(Obd2DeviceRegistrationPersistenceAssembler::toDomainEntity);
    }

    @Override
    public List<Obd2DeviceRegistration> findAllByBranchIdAndStatus(BranchId branchId, Obd2RegistrationStatus status) {
        return persistenceRepository.findAllByBranchIdAndStatus(branchId, status.value())
                .stream()
                .map(Obd2DeviceRegistrationPersistenceAssembler::toDomainEntity)
                .toList();
    }
}
