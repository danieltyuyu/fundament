package com.forkdevs.driveos.platform.billing.application.commandservices;

import com.forkdevs.driveos.platform.billing.domain.model.aggregates.Quote;
import com.forkdevs.driveos.platform.billing.domain.model.commands.CreateQuoteCommand;
import com.forkdevs.driveos.platform.shared.application.result.Result;
import com.forkdevs.driveos.platform.billing.domain.model.valueobjects.QuoteCommandFailure;

/**
 * Application service contract for executing Quote command operations.
 * Returns a Result mapping either the successfully created aggregate or a specific business failure.
 */
public interface QuoteCommandService {
    
    /**
     * Creates a new Quote based on an existing Work Order.
     * Validates the existence of the Work Order and calculates the total amount considering the given discount.
     * 
     * @param command The command object containing the work order ID, branch ID, and discount percentage.
     * @return A Result containing the created Quote if successful, or a failure reason otherwise.
     */
    Result<Quote, QuoteCommandFailure> handle(CreateQuoteCommand command);

    /**
     * Updates the discount percentage of an existing Quote.
     * Validates that the Quote exists, is in DRAFT state, and the new discount is valid.
     * 
     * @param command The command object containing the quote ID and new discount percentage.
     * @return A Result containing the updated Quote if successful, or a failure reason otherwise.
     */
    Result<Quote, QuoteCommandFailure> handle(com.forkdevs.driveos.platform.billing.domain.model.commands.UpdateQuoteDiscountCommand command);

    /**
     * Approves an existing Quote.
     * Validates that the Quote exists and is currently in DRAFT state.
     * 
     * @param command The command object containing the quote ID to approve.
     * @return A Result containing the approved Quote if successful, or a failure reason otherwise.
     */
    Result<Quote, QuoteCommandFailure> handle(com.forkdevs.driveos.platform.billing.domain.model.commands.ApproveQuoteCommand command);

    /**
     * Cancels an existing Quote.
     * Validates that the Quote exists and is not in APPROVED state.
     * 
     * @param command The command object containing the quote ID to cancel.
     * @return A Result containing the canceled Quote if successful, or a failure reason otherwise.
     */
    Result<Quote, QuoteCommandFailure> handle(com.forkdevs.driveos.platform.billing.domain.model.commands.CancelQuoteCommand command);
}
