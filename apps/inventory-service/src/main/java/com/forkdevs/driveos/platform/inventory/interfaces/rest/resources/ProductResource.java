package com.forkdevs.driveos.platform.inventory.interfaces.rest.resources;

import java.util.UUID;

public record ProductResource(
        UUID id,
        String branchId,
        String category,
        String name,
        String sku,
        String description,
        Double salePrice,
        Integer minimumStock,
        Integer currentStock
) {
}
