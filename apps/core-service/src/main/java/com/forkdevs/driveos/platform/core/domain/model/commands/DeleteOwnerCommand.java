package com.forkdevs.driveos.platform.core.domain.model.commands;

import com.forkdevs.driveos.platform.core.domain.model.valueobjects.OwnerId;

public record DeleteOwnerCommand(OwnerId ownerId) {
}
