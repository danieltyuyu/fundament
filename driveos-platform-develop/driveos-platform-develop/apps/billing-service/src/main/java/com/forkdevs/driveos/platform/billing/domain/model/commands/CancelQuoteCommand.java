package com.forkdevs.driveos.platform.billing.domain.model.commands;

import java.util.UUID;

/**
 * Command representing the intent to cancel a Quote.
 * 
 * @param quoteId The unique identifier of the quote to be canceled.
 */
public record CancelQuoteCommand(UUID quoteId) {
    public CancelQuoteCommand {
        if (quoteId == null) {
            throw new IllegalArgumentException("billing.error.command.quoteIdRequired");
        }
    }
}
