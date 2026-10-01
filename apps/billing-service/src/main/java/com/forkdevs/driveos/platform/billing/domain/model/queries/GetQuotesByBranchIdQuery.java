package com.forkdevs.driveos.platform.billing.domain.model.queries;

import com.forkdevs.driveos.platform.shared.domain.model.valueobjects.BranchId;

/**
 * Query to find all Quotes associated with a specific Branch.
 *
 * @param branchId The Value Object representing the unique identifier of the Branch.
 */
public record GetQuotesByBranchIdQuery(BranchId branchId) {
    public GetQuotesByBranchIdQuery {
        if (branchId == null) {
            throw new IllegalArgumentException("billing.error.query.branchIdRequired");
        }
    }
}
