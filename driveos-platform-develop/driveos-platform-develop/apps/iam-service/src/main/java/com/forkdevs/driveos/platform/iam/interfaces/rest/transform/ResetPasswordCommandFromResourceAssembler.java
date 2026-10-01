package com.forkdevs.driveos.platform.iam.interfaces.rest.transform;

import com.forkdevs.driveos.platform.iam.domain.model.commands.ResetPasswordCommand;
import com.forkdevs.driveos.platform.iam.interfaces.rest.resources.ResetPasswordResource;

public class ResetPasswordCommandFromResourceAssembler {
    public static ResetPasswordCommand toCommandFromResource(ResetPasswordResource resource) {
        return new ResetPasswordCommand(resource.token(), new com.forkdevs.driveos.platform.iam.domain.model.valueobjects.Password(resource.newPassword()));
    }
}
