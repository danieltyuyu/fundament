package com.forkdevs.driveos.platform.iam.domain.model.commands;

import com.forkdevs.driveos.platform.iam.domain.model.valueobjects.EmailAddress;

public record GeneratePasswordRecoveryTokenCommand(EmailAddress email) {
}
