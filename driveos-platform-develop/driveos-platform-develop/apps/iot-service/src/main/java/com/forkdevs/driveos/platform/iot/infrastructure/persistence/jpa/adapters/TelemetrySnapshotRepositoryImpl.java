package com.forkdevs.driveos.platform.iot.infrastructure.persistence.jpa.adapters;

import com.forkdevs.driveos.platform.iot.domain.model.aggregates.TelemetrySnapshot;
import com.forkdevs.driveos.platform.iot.domain.model.valueobjects.Obd2DeviceRegistrationId;
import com.forkdevs.driveos.platform.iot.domain.model.valueobjects.TelemetrySnapshotId;
import com.forkdevs.driveos.platform.iot.domain.repositories.TelemetrySnapshotRepository;
import com.forkdevs.driveos.platform.iot.infrastructure.persistence.jpa.assemblers.TelemetrySnapshotPersistenceAssembler;
import com.forkdevs.driveos.platform.iot.infrastructure.persistence.jpa.entities.TelemetrySnapshotPersistenceEntity;
import com.forkdevs.driveos.platform.iot.infrastructure.persistence.jpa.repositories.TelemetrySnapshotPersistenceRepository;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Repository;

import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Repository
public class TelemetrySnapshotRepositoryImpl implements TelemetrySnapshotRepository {

    private final TelemetrySnapshotPersistenceRepository persistenceRepository;
    private final ApplicationEventPublisher eventPublisher;

    public TelemetrySnapshotRepositoryImpl(
            TelemetrySnapshotPersistenceRepository persistenceRepository,
            ApplicationEventPublisher eventPublisher
    ) {
        this.persistenceRepository = persistenceRepository;
        this.eventPublisher = eventPublisher;
    }

    @Override
    public TelemetrySnapshot save(TelemetrySnapshot telemetrySnapshot) {
        TelemetrySnapshotPersistenceEntity entity = TelemetrySnapshotPersistenceAssembler.toPersistenceEntity(telemetrySnapshot);
        TelemetrySnapshotPersistenceEntity savedEntity = persistenceRepository.save(entity);
        TelemetrySnapshot savedSnapshot = TelemetrySnapshotPersistenceAssembler.toDomainEntity(savedEntity);

        if (savedSnapshot != null) {
            telemetrySnapshot.domainEvents().forEach(eventPublisher::publishEvent);
            telemetrySnapshot.clearDomainEvents();
        }
        return savedSnapshot;
    }

    @Override
    public List<TelemetrySnapshot> saveAll(List<TelemetrySnapshot> telemetrySnapshots) {
        List<TelemetrySnapshotPersistenceEntity> entities = telemetrySnapshots.stream()
                .map(TelemetrySnapshotPersistenceAssembler::toPersistenceEntity)
                .collect(Collectors.toList());

        List<TelemetrySnapshotPersistenceEntity> savedEntities = persistenceRepository.saveAll(entities);

        List<TelemetrySnapshot> savedSnapshots = savedEntities.stream()
                .map(TelemetrySnapshotPersistenceAssembler::toDomainEntity)
                .collect(Collectors.toList());

        // Publish events for all
        for (int i = 0; i < telemetrySnapshots.size(); i++) {
            telemetrySnapshots.get(i).domainEvents().forEach(eventPublisher::publishEvent);
            telemetrySnapshots.get(i).clearDomainEvents();
        }

        return savedSnapshots;
    }

    @Override
    public Optional<TelemetrySnapshot> findById(TelemetrySnapshotId id) {
        return persistenceRepository.findById(id.value())
                .map(TelemetrySnapshotPersistenceAssembler::toDomainEntity);
    }

    @Override
    public Optional<TelemetrySnapshot> findLatestByRegistrationId(Obd2DeviceRegistrationId registrationId) {
        return persistenceRepository.findFirstByObd2DeviceRegistrationIdOrderByCreatedAtDesc(registrationId)
                .map(TelemetrySnapshotPersistenceAssembler::toDomainEntity);
    }

    @Override
    public List<TelemetrySnapshot> findAllByRegistrationId(Obd2DeviceRegistrationId registrationId) {
        return persistenceRepository.findAllByObd2DeviceRegistrationIdOrderByCreatedAtDesc(registrationId).stream()
                .map(TelemetrySnapshotPersistenceAssembler::toDomainEntity)
                .collect(Collectors.toList());
    }

    @Override
    public List<TelemetrySnapshot> findAllByRegistrationIdAndCreatedAtGreaterThanEqual(
            Obd2DeviceRegistrationId registrationId,
            Instant startTimestamp
    ) {
        return persistenceRepository.findAllByObd2DeviceRegistrationIdAndCreatedAtGreaterThanEqualOrderByCreatedAtDesc(
                registrationId,
                startTimestamp
        ).stream()
                .map(TelemetrySnapshotPersistenceAssembler::toDomainEntity)
                .collect(Collectors.toList());
    }
}
