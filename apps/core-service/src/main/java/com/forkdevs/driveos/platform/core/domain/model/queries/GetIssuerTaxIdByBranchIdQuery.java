package com.forkdevs.driveos.platform.core.domain.model.queries;

import com.forkdevs.driveos.platform.shared.domain.model.valueobjects.BranchId;

public record GetIssuerTaxIdByBranchIdQuery(BranchId branchId) {
    public GetIssuerTaxIdByBranchIdQuery {
        if (branchId == null) {
            throw new IllegalArgumentException("shared.error.branchId.required");
        }
    }
}
