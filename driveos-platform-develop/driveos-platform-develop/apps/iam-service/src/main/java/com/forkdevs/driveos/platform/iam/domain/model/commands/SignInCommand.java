package com.forkdevs.driveos.platform.iam.domain.model.commands;

import com.forkdevs.driveos.platform.iam.domain.model.valueobjects.EmailAddress;
import com.forkdevs.driveos.platform.iam.domain.model.valueobjects.Password;

public record SignInCommand(EmailAddress email, Password password) {
}
