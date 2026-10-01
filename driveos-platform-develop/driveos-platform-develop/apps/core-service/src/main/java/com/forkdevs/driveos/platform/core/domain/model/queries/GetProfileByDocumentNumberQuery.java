package com.forkdevs.driveos.platform.core.domain.model.queries;

public record GetProfileByDocumentNumberQuery(String documentNumber) {
    public GetProfileByDocumentNumberQuery {
        if (documentNumber == null || documentNumber.isBlank()) {
            throw new IllegalArgumentException("documentNumber cannot be null or blank");
        }
    }
}
