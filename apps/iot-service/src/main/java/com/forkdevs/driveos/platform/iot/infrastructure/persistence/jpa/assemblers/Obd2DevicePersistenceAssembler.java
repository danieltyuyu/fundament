package com.forkdevs.driveos.platform.iot.infrastructure.persistence.jpa.assemblers;

import com.forkdevs.driveos.platform.iot.domain.model.aggregates.Obd2Device;
import com.forkdevs.driveos.platform.iot.domain.model.valueobjects.Obd2DeviceId;
import com.forkdevs.driveos.platform.iot.domain.model.valueobjects.Obd2DeviceStatus;
import com.forkdevs.driveos.platform.iot.infrastructure.persistence.jpa.entities.Obd2DevicePersistenceEntity;

public class Obd2DevicePersistenceAssembler {

    public static Obd2DevicePersistenceEntity toPersistenceEntity(Obd2Device domain) {
        if (domain == null) {
            return null;
        }
        Obd2DevicePersistenceEntity entity = new Obd2DevicePersistenceEntity();
        entity.setId(domain.getId() != null ? domain.getId().value() : null);
        entity.setBranchId(domain.getBranchId());
        entity.setMacAddress(domain.getMacAddress());
        entity.setLastPing(domain.getLastPing());
        entity.setStatus(domain.getStatus() != null ? domain.getStatus().value() : null);
        entity.setVersion(domain.getVersion());
        return entity;
    }

    public static Obd2Device toDomainEntity(Obd2DevicePersistenceEntity entity) {
        if (entity == null) {
            return null;
        }
        return new Obd2Device(
                new Obd2DeviceId(entity.getId()),
                entity.getBranchId(),
                entity.getMacAddress(),
                entity.getLastPing(),
                entity.getStatus() != null ? new Obd2DeviceStatus(entity.getStatus()) : null,
                entity.getVersion()
        );
    }
}
