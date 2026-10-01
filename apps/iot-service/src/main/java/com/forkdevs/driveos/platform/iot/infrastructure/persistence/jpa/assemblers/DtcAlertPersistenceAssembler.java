package com.forkdevs.driveos.platform.iot.infrastructure.persistence.jpa.assemblers;

import com.forkdevs.driveos.platform.iot.domain.model.aggregates.DtcAlert;
import com.forkdevs.driveos.platform.iot.domain.model.valueobjects.DtcAlertId;
import com.forkdevs.driveos.platform.iot.domain.model.valueobjects.DtcAlertSeverity;
import com.forkdevs.driveos.platform.iot.infrastructure.persistence.jpa.entities.DtcAlertPersistenceEntity;

/**
 * Assembler to translate between DtcAlert persistence entity and domain aggregate.
 */
public class DtcAlertPersistenceAssembler {

    public static DtcAlert toDomainEntity(DtcAlertPersistenceEntity entity) {
        if (entity == null) {
            return null;
        }
        return new DtcAlert(
                new DtcAlertId(entity.getId()),
                entity.getTelemetrySnapshotId(),
                entity.getBranchId(),
                entity.getDtcCode(),
                entity.getDescription(),
                new DtcAlertSeverity(entity.getSeverity()),
                entity.getCreatedAt()
        );
    }
}
