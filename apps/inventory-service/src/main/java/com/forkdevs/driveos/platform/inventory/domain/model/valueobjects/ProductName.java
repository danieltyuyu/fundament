package com.forkdevs.driveos.platform.inventory.domain.model.valueobjects;

public record ProductName(String name) {
    public ProductName {
        if (name == null || name.isBlank()) {
            throw new IllegalArgumentException("inventory.error.productName.required");
        }
    }
}
