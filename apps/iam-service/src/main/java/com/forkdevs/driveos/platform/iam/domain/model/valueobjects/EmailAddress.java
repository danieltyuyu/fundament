package com.forkdevs.driveos.platform.iam.domain.model.valueobjects;

public record EmailAddress(String value) {
    public EmailAddress {
        if (value == null || value.isBlank()) {
            throw new IllegalArgumentException("iam.error.email.required");
        }
    }
}
