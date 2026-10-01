package com.forkdevs.driveos.platform.billing.application.queryservices;

import com.forkdevs.driveos.platform.billing.domain.model.aggregates.Quote;
import com.forkdevs.driveos.platform.billing.domain.model.queries.GetQuoteByIdQuery;

import java.util.Optional;

/**
 * Application service contract for executing Quote query operations.
 * Handles read-only operations related to Quotes.
 */
public interface QuoteQueryService {
    
    /**
     * Handles the retrieval of a Quote by its unique identifier.
     * 
     * @param query The query object containing the quote ID.
     * @return An Optional containing the Quote if found, or empty otherwise.
     */
    Optional<Quote> handle(GetQuoteByIdQuery query);

    /**
     * Handles the retrieval of all Quotes associated with a specific Branch.
     * 
     * @param query The query object containing the branch ID.
     * @return A list of Quotes belonging to the branch, or an empty list if none are found.
     */
    java.util.List<Quote> handle(com.forkdevs.driveos.platform.billing.domain.model.queries.GetQuotesByBranchIdQuery query);
}
