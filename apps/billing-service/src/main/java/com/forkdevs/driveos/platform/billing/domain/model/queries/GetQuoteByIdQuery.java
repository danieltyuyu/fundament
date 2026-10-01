package com.forkdevs.driveos.platform.billing.domain.model.queries;

import java.util.UUID;

/**
 * Query to find a Quote by its unique identifier.
 *
 * @param quoteId The UUID of the Quote to find.
 */
public record GetQuoteByIdQuery(UUID quoteId) {
    public GetQuoteByIdQuery {
        if (quoteId == null) {
            throw new IllegalArgumentException("billing.error.query.quoteIdRequired");
        }
    }
}
