package com.forkdevs.driveos.platform.core.domain.model.valueobjects;

import java.util.UUID;

public record UserId(UUID value) {
    public UserId {
        if (value == null) {
            throw new IllegalArgumentException("core.error.userId.required");
        }
    }
}
