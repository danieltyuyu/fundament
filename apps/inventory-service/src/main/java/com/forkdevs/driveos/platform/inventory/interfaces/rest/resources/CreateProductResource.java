package com.forkdevs.driveos.platform.inventory.interfaces.rest.resources;

public record CreateProductResource(
        String branchId,
        String category,
        String name,
        String sku,
        String description,
        Double salePrice,
        Integer minimumStock
) {
}
