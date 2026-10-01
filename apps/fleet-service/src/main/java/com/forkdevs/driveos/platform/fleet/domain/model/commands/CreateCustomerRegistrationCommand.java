package com.forkdevs.driveos.platform.fleet.domain.model.commands;

import com.forkdevs.driveos.platform.shared.domain.model.valueobjects.BranchId;
import com.forkdevs.driveos.platform.shared.domain.model.valueobjects.CustomerId;

public record CreateCustomerRegistrationCommand(
        CustomerId customerId,
        BranchId branchId
) {
    public CreateCustomerRegistrationCommand {
        if (customerId == null) throw new IllegalArgumentException("Customer ID is required");
        if (branchId == null) throw new IllegalArgumentException("Branch ID is required");
    }
}

