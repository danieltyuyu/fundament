package com.forkdevs.driveos.platform.iam.domain.model.queries;

import com.forkdevs.driveos.platform.iam.domain.model.aggregates.User;

public record AuthenticatedUser(User user, String token) {
}
