package com.forkdevs.driveos.platform.core.domain.model.valueobjects;

import java.util.UUID;

public record SubscriptionPlanId(UUID value) {
    public SubscriptionPlanId {
        if (value == null) {
            throw new IllegalArgumentException("core.error.subscriptionPlanId.required");
        }
    }
}
