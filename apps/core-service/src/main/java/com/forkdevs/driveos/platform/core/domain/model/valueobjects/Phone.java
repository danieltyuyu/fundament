package com.forkdevs.driveos.platform.core.domain.model.valueobjects;

public record Phone(String value) {
    public Phone {
        if (value == null || value.isBlank()) {
            throw new IllegalArgumentException("core.error.phone.required");
        }
    }
}
