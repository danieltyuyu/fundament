package com.forkdevs.driveos.platform.inventory.domain.model.events;

import com.forkdevs.driveos.platform.shared.domain.model.valueobjects.BranchId;

public record WorkOrderPaidEvent(
        Object source,
        BranchId branchId
) {}
