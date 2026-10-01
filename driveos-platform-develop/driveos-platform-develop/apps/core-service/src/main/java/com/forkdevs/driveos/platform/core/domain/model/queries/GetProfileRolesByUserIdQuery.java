package com.forkdevs.driveos.platform.core.domain.model.queries;

import com.forkdevs.driveos.platform.core.domain.model.valueobjects.UserId;

public record GetProfileRolesByUserIdQuery(UserId userId) {
    public GetProfileRolesByUserIdQuery {
        if (userId == null) {
            throw new IllegalArgumentException("userId cannot be null");
        }
    }
}
