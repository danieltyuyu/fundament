package com.forkdevs.driveos.platform.billing.domain.model.queries;

import com.forkdevs.driveos.platform.shared.domain.model.valueobjects.BranchId;

/**
 * Query to retrieve all Vouchers emitted in a specific branch.
 * 
 * @param branchId the unique identifier of the branch
 */
public record GetVouchersByBranchIdQuery(BranchId branchId) {
    public GetVouchersByBranchIdQuery {
        if (branchId == null) {
            throw new IllegalArgumentException("billing.error.query.branchIdRequired");
        }
    }
}
