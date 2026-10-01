package com.forkdevs.driveos.platform.inventory.interfaces.rest.resources;

import java.util.List;

public record ProductDetailsResource(
        String id,
        String branchId,
        String category,
        String name,
        String sku,
        String description,
        Double salePrice,
        Integer minimumStock,
        Integer currentStock,
        List<ProductBatchResource> batches
) {
}
