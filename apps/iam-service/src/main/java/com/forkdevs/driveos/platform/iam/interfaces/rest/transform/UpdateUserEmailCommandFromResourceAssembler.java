package com.forkdevs.driveos.platform.iam.interfaces.rest.transform;

import com.forkdevs.driveos.platform.iam.domain.model.commands.UpdateUserEmailCommand;
import com.forkdevs.driveos.platform.iam.interfaces.rest.resources.UpdateUserEmailResource;

import java.util.UUID;
import com.forkdevs.driveos.platform.iam.domain.model.valueobjects.EmailAddress;
import com.forkdevs.driveos.platform.iam.domain.model.valueobjects.UserId;

public class UpdateUserEmailCommandFromResourceAssembler {
    public static UpdateUserEmailCommand toCommandFromResource(UUID userId, UpdateUserEmailResource resource) {
        return new UpdateUserEmailCommand(
                new UserId(userId),
                new EmailAddress(resource.email())
        );
    }
}
