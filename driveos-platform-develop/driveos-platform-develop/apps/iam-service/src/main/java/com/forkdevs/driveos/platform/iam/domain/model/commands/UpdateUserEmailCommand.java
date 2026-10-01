package com.forkdevs.driveos.platform.iam.domain.model.commands;

import com.forkdevs.driveos.platform.iam.domain.model.valueobjects.EmailAddress;
import com.forkdevs.driveos.platform.iam.domain.model.valueobjects.UserId;

public record UpdateUserEmailCommand(UserId userId, EmailAddress newEmail) {}
