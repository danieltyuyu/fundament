package com.forkdevs.driveos.platform.iot.interfaces.rest.transform;

import com.forkdevs.driveos.platform.iot.domain.model.aggregates.TelemetrySnapshot;
import com.forkdevs.driveos.platform.iot.interfaces.rest.resources.TelemetrySnapshotResource;

public class TelemetrySnapshotResourceFromAggregateAssembler {

    public static TelemetrySnapshotResource toResourceFromAggregate(TelemetrySnapshot aggregate) {
        if (aggregate == null) {
            return null;
        }

        return new TelemetrySnapshotResource(
                aggregate.getId() != null ? aggregate.getId().value() : null,
                aggregate.getObd2DeviceRegistrationId() != null ? aggregate.getObd2DeviceRegistrationId().value() : null,
                aggregate.getBranchId() != null ? aggregate.getBranchId().value() : null,
                aggregate.getRpm(),
                aggregate.getTemperature(),
                aggregate.getSpeedKmh(),
                aggregate.getOdometerKm(),
                aggregate.getFuelLevelPercent(),
                aggregate.getCreatedAt()
        );
    }
}
