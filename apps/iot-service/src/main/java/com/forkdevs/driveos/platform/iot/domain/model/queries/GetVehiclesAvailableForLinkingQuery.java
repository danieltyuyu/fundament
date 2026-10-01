package com.forkdevs.driveos.platform.iot.domain.model.queries;

import com.forkdevs.driveos.platform.shared.domain.model.valueobjects.BranchId;

/**
 * Query representing the request to retrieve all vehicles in a branch that do not have an active OBD2 device registration.
 * This record is placed inside the iot bounded context.
 */
public record GetVehiclesAvailableForLinkingQuery(
        BranchId branchId
) {
    public GetVehiclesAvailableForLinkingQuery {
        if (branchId == null) {
            throw new IllegalArgumentException("branchId cannot be null");
        }
    }
}
