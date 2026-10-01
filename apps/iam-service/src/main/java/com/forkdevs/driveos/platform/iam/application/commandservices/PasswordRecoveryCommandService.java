package com.forkdevs.driveos.platform.iam.application.commandservices;

import com.forkdevs.driveos.platform.iam.domain.model.commands.GeneratePasswordRecoveryTokenCommand;
import com.forkdevs.driveos.platform.iam.domain.model.commands.ResetPasswordCommand;

public interface PasswordRecoveryCommandService {
    void handle(GeneratePasswordRecoveryTokenCommand command);
    void handle(ResetPasswordCommand command);
}
