package com.forkdevs.driveos.platform.billing.domain.model.commands;

import com.forkdevs.driveos.platform.shared.domain.model.valueobjects.BranchId;
import java.util.UUID;

public record CreateQuoteCommand(
        UUID workOrderId,
        BranchId branchId,
        Double discountPercentage
) {
    public CreateQuoteCommand {
        if (workOrderId == null) {
            throw new IllegalArgumentException("billing.error.command.workOrderIdRequired");
        }
        if (branchId == null) {
            throw new IllegalArgumentException("billing.error.command.branchIdRequired");
        }
        if (discountPercentage == null || discountPercentage < 0 || discountPercentage > 100) {
            throw new IllegalArgumentException("billing.error.quote.invalidDiscount");
        }
    }
}
