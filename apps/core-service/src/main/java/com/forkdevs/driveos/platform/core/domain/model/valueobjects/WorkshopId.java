package com.forkdevs.driveos.platform.core.domain.model.valueobjects;

import java.util.UUID;

public record WorkshopId(UUID value) {
    public WorkshopId {
        if (value == null) {
            throw new IllegalArgumentException("core.error.workshopId.required");
        }
    }
}
