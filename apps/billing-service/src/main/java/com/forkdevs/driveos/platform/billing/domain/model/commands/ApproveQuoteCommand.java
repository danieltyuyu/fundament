package com.forkdevs.driveos.platform.billing.domain.model.commands;

import java.util.UUID;

/**
 * Command representing the intent to approve a Quote.
 * 
 * @param quoteId The unique identifier of the quote to be approved.
 */
public record ApproveQuoteCommand(UUID quoteId) {
    public ApproveQuoteCommand {
        if (quoteId == null) {
            throw new IllegalArgumentException("billing.error.command.quoteIdRequired");
        }
    }
}
