package com.forkdevs.driveos.platform.inventory.domain.model.valueobjects;

public record Sku(String value) {
    public Sku {
        if (value == null || value.isBlank()) {
            throw new IllegalArgumentException("inventory.error.sku.required");
        }
    }
}
