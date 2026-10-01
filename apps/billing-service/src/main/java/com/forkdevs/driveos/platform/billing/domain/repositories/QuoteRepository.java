package com.forkdevs.driveos.platform.billing.domain.repositories;

import com.forkdevs.driveos.platform.billing.domain.model.aggregates.Quote;
import com.forkdevs.driveos.platform.shared.domain.model.valueobjects.BranchId;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * Domain repository contract for Quote aggregates.
 * Defines the operations for persisting and retrieving quotes.
 */
public interface QuoteRepository {
    
    /**
     * Saves a Quote aggregate to the persistence layer.
     * 
     * @param quote The Quote aggregate to save.
     * @return The saved Quote aggregate.
     */
    Quote save(Quote quote);

    /**
     * Finds a Quote by its unique identifier.
     * 
     * @param id The unique identifier of the quote.
     * @return An Optional containing the Quote if found, or empty otherwise.
     */
    Optional<Quote> findById(UUID id);

    /**
     * Finds all Quotes associated with a specific branch.
     * 
     * @param branchId The Value Object representing the branch identifier.
     * @return A list of Quotes belonging to the given branch.
     */
    List<Quote> findAllByBranchId(BranchId branchId);
}
