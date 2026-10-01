package com.forkdevs.driveos.platform.billing.interfaces.rest.transform;

import com.forkdevs.driveos.platform.billing.domain.model.commands.UpdateQuoteDiscountCommand;
import com.forkdevs.driveos.platform.billing.interfaces.rest.resources.UpdateQuoteResource;

import java.util.UUID;

/**
 * Assembler to convert an UpdateQuoteResource to an UpdateQuoteDiscountCommand.
 */
public class UpdateQuoteCommandFromResourceAssembler {

    /**
     * Converts a REST resource into a domain command for updating a quote's discount.
     * 
     * @param quoteId The ID of the quote from the URL path.
     * @param resource The resource payload from the request body.
     * @return The instantiated UpdateQuoteDiscountCommand.
     */
    public static UpdateQuoteDiscountCommand toCommandFromResource(UUID quoteId, UpdateQuoteResource resource) {
        return new UpdateQuoteDiscountCommand(quoteId, resource.discountPercentage());
    }
}
