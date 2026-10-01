package com.forkdevs.driveos.platform.iot.infrastructure.persistence.jpa.assemblers;

import com.forkdevs.driveos.platform.iot.domain.model.aggregates.TelemetrySnapshot;
import com.forkdevs.driveos.platform.iot.domain.model.valueobjects.TelemetrySnapshotId;
import com.forkdevs.driveos.platform.iot.infrastructure.persistence.jpa.entities.TelemetrySnapshotPersistenceEntity;

public class TelemetrySnapshotPersistenceAssembler {

    public static TelemetrySnapshotPersistenceEntity toPersistenceEntity(TelemetrySnapshot domain) {
        if (domain == null) {
            return null;
        }
        TelemetrySnapshotPersistenceEntity entity = new TelemetrySnapshotPersistenceEntity();
        entity.setId(domain.getId() != null ? domain.getId().value() : null);
        entity.setObd2DeviceRegistrationId(domain.getObd2DeviceRegistrationId());
        entity.setBranchId(domain.getBranchId());
        entity.setRpm(domain.getRpm());
        entity.setTemperature(domain.getTemperature());
        entity.setSpeedKmh(domain.getSpeedKmh());
        entity.setOdometerKm(domain.getOdometerKm());
        entity.setFuelLevelPercent(domain.getFuelLevelPercent());
        entity.setCreatedAt(domain.getCreatedAt());
        return entity;
    }

    public static TelemetrySnapshot toDomainEntity(TelemetrySnapshotPersistenceEntity entity) {
        if (entity == null) {
            return null;
        }
        return new TelemetrySnapshot(
                new TelemetrySnapshotId(entity.getId()),
                entity.getObd2DeviceRegistrationId(),
                entity.getBranchId(),
                entity.getRpm(),
                entity.getTemperature(),
                entity.getSpeedKmh(),
                entity.getOdometerKm(),
                entity.getFuelLevelPercent(),
                entity.getCreatedAt()
        );
    }
}
