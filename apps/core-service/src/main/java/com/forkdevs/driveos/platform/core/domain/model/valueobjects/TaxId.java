package com.forkdevs.driveos.platform.core.domain.model.valueobjects;

public record TaxId(String value) {
    public TaxId {
        if(value == null || !value.matches("\\d{11}")) {
            throw new IllegalArgumentException("core.error.taxId.invalid");
        }
    }
}
