package com.forkdevs.driveos.platform.inventory.domain.model.valueobjects;

public record ProductCategory(String value) {
    public ProductCategory {
        if (value == null || value.isBlank()) {
            throw new IllegalArgumentException("inventory.error.productCategory.required");
        }
    }
}
