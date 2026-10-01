package com.forkdevs.driveos.platform.iot.interfaces.rest.transform;

import com.forkdevs.driveos.platform.iot.domain.model.aggregates.DtcAlert;
import com.forkdevs.driveos.platform.iot.interfaces.rest.resources.DtcAlertResource;

/**
 * Assembler to translate DtcAlert domain aggregate to DtcAlertResource DTO.
 */
public class DtcAlertResourceFromAggregateAssembler {

    public static DtcAlertResource toResourceFromAggregate(DtcAlert aggregate) {
        return new DtcAlertResource(
                aggregate.getId() != null ? aggregate.getId().value() : null,
                aggregate.getTelemetrySnapshotId(),
                aggregate.getBranchId() != null ? aggregate.getBranchId().value() : null,
                aggregate.getDtcCode(),
                aggregate.getDescription(),
                aggregate.getSeverity() != null ? aggregate.getSeverity().value() : null,
                aggregate.getCreatedAt()
        );
    }
}
