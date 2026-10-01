package com.forkdevs.driveos.platform.core.interfaces.rest.resources;

import java.util.Date;
import java.util.UUID;

public record BranchSubscriptionResource(
        UUID id,
        UUID branchId,
        UUID planId,
        String billingCycle,
        String status,
        Date startDate,
        Date endDate
) {
}
