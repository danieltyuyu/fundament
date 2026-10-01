package com.forkdevs.driveos.platform.core.domain.model.queries.responses;

import java.util.UUID;

public record ProfileSummary(
        UUID profileId,
        UUID userId,
        String firstName,
        String lastName,
        String documentType,
        String documentNumber,
        String profileType
) {
}
