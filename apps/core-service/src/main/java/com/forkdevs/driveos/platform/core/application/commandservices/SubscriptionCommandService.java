package com.forkdevs.driveos.platform.core.application.commandservices;

import com.forkdevs.driveos.platform.core.domain.model.commands.AssignSubscriptionCommand;
import com.forkdevs.driveos.platform.core.domain.model.commands.CancelSubscriptionCommand;
import com.forkdevs.driveos.platform.core.domain.model.aggregates.BranchSubscription;

import java.util.Optional;

public interface SubscriptionCommandService {
    Optional<BranchSubscription> handle(AssignSubscriptionCommand command);
    Optional<BranchSubscription> handle(CancelSubscriptionCommand command);
}
