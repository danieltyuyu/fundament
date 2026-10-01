package com.forkdevs.driveos.platform.inventory.domain.model.commands;

import com.forkdevs.driveos.platform.inventory.domain.model.valueobjects.InventoryQuantity;
import com.forkdevs.driveos.platform.shared.domain.model.valueobjects.Money;

import java.util.UUID;

public record AddBatchToProductCommand(
        UUID productId,
        InventoryQuantity quantity,
        Money acquisitionCost
) {
}
