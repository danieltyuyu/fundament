package com.forkdevs.driveos.platform.core.domain.model.valueobjects;

import java.util.UUID;

public record BranchSubscriptionId(UUID value) {
    public BranchSubscriptionId {
        if (value == null) {
            throw new IllegalArgumentException("core.error.branchSubscriptionId.required");
        }
    }
}
