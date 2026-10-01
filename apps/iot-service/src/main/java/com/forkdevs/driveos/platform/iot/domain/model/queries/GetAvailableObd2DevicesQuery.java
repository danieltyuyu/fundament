package com.forkdevs.driveos.platform.iot.domain.model.queries;

import com.forkdevs.driveos.platform.shared.domain.model.valueobjects.BranchId;

/**
 * Query representing the request to retrieve all available OBD2 devices belonging to a specific branch.
 */
public record GetAvailableObd2DevicesQuery(
        BranchId branchId
) {
    public GetAvailableObd2DevicesQuery {
        if (branchId == null) {
            throw new IllegalArgumentException("branchId cannot be null");
        }
    }
}
