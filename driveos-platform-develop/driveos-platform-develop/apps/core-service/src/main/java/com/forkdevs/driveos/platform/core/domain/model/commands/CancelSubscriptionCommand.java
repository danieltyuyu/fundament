package com.forkdevs.driveos.platform.core.domain.model.commands;

import com.forkdevs.driveos.platform.shared.domain.model.valueobjects.BranchId;

public record CancelSubscriptionCommand(
        BranchId branchId
) {
}

