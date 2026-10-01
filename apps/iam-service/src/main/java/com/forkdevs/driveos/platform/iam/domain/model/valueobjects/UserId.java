package com.forkdevs.driveos.platform.iam.domain.model.valueobjects;

import java.util.UUID;

public record UserId(UUID value) {
    public UserId {
        if (value == null) {
            throw new IllegalArgumentException("iam.error.userId.required");
        }
    }
}
