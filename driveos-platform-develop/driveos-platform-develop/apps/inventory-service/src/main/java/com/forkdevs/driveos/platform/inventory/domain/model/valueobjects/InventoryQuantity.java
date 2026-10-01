package com.forkdevs.driveos.platform.inventory.domain.model.valueobjects;

public record InventoryQuantity(Integer value) {
    public InventoryQuantity {
        if (value == null || value < 0) {
            throw new IllegalArgumentException("inventory.error.quantity.invalid");
        }
    }

    public InventoryQuantity add(InventoryQuantity quantity) {
        return new InventoryQuantity(this.value + quantity.value());
    }

    public InventoryQuantity subtract(InventoryQuantity quantity) {
        if (this.value < quantity.value()) {
            throw new IllegalArgumentException("inventory.error.quantity.resultingNegative");
        }
        return new InventoryQuantity(this.value - quantity.value());
    }
}
