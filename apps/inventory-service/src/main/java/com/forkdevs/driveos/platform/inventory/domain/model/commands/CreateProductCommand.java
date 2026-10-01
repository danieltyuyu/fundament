package com.forkdevs.driveos.platform.inventory.domain.model.commands;

import com.forkdevs.driveos.platform.inventory.domain.model.valueobjects.InventoryQuantity;
import com.forkdevs.driveos.platform.inventory.domain.model.valueobjects.ProductCategory;
import com.forkdevs.driveos.platform.inventory.domain.model.valueobjects.ProductName;
import com.forkdevs.driveos.platform.inventory.domain.model.valueobjects.Sku;
import com.forkdevs.driveos.platform.shared.domain.model.valueobjects.BranchId;
import com.forkdevs.driveos.platform.shared.domain.model.valueobjects.Money;

public record CreateProductCommand(
        BranchId branchId,
        ProductCategory category,
        ProductName name,
        Sku sku,
        String description,
        Money salePrice,
        InventoryQuantity minimumStock
) {
}
