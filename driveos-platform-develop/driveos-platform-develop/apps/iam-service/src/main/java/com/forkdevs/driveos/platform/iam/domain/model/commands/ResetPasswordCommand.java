package com.forkdevs.driveos.platform.iam.domain.model.commands;

import com.forkdevs.driveos.platform.iam.domain.model.valueobjects.Password;

public record ResetPasswordCommand(String token, Password newPassword) {
}
