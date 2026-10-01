package com.forkdevs.driveos.platform.iot.domain.model.queries;

import com.forkdevs.driveos.platform.iot.domain.model.valueobjects.Obd2RegistrationStatus;
import com.forkdevs.driveos.platform.shared.domain.model.valueobjects.BranchId;

/**
 * Query representing the request to retrieve all OBD2 device registrations
 * belonging to a specific branch and filtered by registration status.
 */
public record GetObd2DeviceRegistrationsByBranchIdAndStatusQuery(
        BranchId branchId,
        Obd2RegistrationStatus status
) {
    public GetObd2DeviceRegistrationsByBranchIdAndStatusQuery {
        if (branchId == null) {
            throw new IllegalArgumentException("branchId cannot be null");
        }
        if (status == null) {
            throw new IllegalArgumentException("status cannot be null");
        }
    }
}
