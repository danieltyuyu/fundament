package com.forkdevs.driveos.platform.core.application.commandservices;

import com.forkdevs.driveos.platform.core.domain.model.aggregates.Owner;
import com.forkdevs.driveos.platform.core.domain.model.commands.CreateOwnerCommand;
import com.forkdevs.driveos.platform.core.domain.model.commands.DeleteOwnerCommand;
import com.forkdevs.driveos.platform.core.domain.model.commands.UpdateOwnerCommand;

import java.util.Optional;

public interface OwnerCommandService {
    Optional<Owner> handle(CreateOwnerCommand command);
    Optional<Owner> handle(UpdateOwnerCommand command);
    void handle(DeleteOwnerCommand command);
}
