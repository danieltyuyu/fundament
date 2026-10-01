package com.forkdevs.driveos.platform.core.domain.model.commands;

import com.forkdevs.driveos.platform.core.domain.model.valueobjects.BillingCycle;
import com.forkdevs.driveos.platform.shared.domain.model.valueobjects.BranchId;
import com.forkdevs.driveos.platform.core.domain.model.valueobjects.SubscriptionPlanId;

public record AssignSubscriptionCommand(
        BranchId branchId,
        SubscriptionPlanId planId,
        BillingCycle billingCycle,
        com.forkdevs.driveos.platform.core.domain.model.valueobjects.CreditCard creditCard
) {
}

