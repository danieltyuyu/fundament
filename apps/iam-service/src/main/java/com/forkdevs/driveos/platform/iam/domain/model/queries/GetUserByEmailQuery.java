package com.forkdevs.driveos.platform.iam.domain.model.queries;

import com.forkdevs.driveos.platform.iam.domain.model.valueobjects.EmailAddress;

public record GetUserByEmailQuery(EmailAddress email) {
}
