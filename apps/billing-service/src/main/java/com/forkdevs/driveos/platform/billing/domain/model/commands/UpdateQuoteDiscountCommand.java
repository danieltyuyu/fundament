package com.forkdevs.driveos.platform.billing.domain.model.commands;

import java.util.UUID;

/**
 * Command representing the intent to update the discount percentage of a Quote.
 * 
 * @param quoteId The unique identifier of the quote to update.
 * @param discountPercentage The new discount percentage (between 0 and 100).
 */
public record UpdateQuoteDiscountCommand(UUID quoteId, Double discountPercentage) {
    public UpdateQuoteDiscountCommand {
        if (quoteId == null) {
            throw new IllegalArgumentException("billing.error.command.quoteIdRequired");
        }
        if (discountPercentage == null) {
            throw new IllegalArgumentException("billing.error.command.discountRequired");
        }
    }
}
