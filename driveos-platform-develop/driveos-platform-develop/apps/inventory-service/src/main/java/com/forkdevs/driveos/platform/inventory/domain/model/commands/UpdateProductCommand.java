package com.forkdevs.driveos.platform.inventory.domain.model.commands;

import com.forkdevs.driveos.platform.inventory.domain.model.valueobjects.ProductCategory;
import com.forkdevs.driveos.platform.inventory.domain.model.valueobjects.ProductName;
import com.forkdevs.driveos.platform.inventory.domain.model.valueobjects.Sku;

import java.util.UUID;

public record UpdateProductCommand(
        UUID productId,
        ProductName name,
        ProductCategory category,
        Sku sku,
        String description,
        com.forkdevs.driveos.platform.shared.domain.model.valueobjects.Money salePrice,
        com.forkdevs.driveos.platform.inventory.domain.model.valueobjects.InventoryQuantity minimumStock
) {
}
