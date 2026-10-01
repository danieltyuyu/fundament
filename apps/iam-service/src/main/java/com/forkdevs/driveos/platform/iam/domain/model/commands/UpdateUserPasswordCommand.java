package com.forkdevs.driveos.platform.iam.domain.model.commands;

import com.forkdevs.driveos.platform.iam.domain.model.valueobjects.Password;
import com.forkdevs.driveos.platform.iam.domain.model.valueobjects.UserId;

public record UpdateUserPasswordCommand(
        UserId userId,
        Password currentPassword,
        Password newPassword
) {
}
