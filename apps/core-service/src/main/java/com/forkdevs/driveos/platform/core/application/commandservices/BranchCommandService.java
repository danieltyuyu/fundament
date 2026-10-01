package com.forkdevs.driveos.platform.core.application.commandservices;

import com.forkdevs.driveos.platform.core.domain.model.aggregates.Branch;
import com.forkdevs.driveos.platform.core.domain.model.commands.CreateBranchCommand;
import com.forkdevs.driveos.platform.core.domain.model.commands.UpdateBranchCommand;

import java.util.Optional;

public interface BranchCommandService {
    Optional<Branch> handle(CreateBranchCommand command);
    Optional<Branch> handle(UpdateBranchCommand command);
}
