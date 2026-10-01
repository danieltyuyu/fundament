package com.forkdevs.driveos.platform.billing.interfaces.rest.resources;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

/**
 * Resource representing the payload to update a quote's discount.
 * 
 * @param discountPercentage The new discount percentage to apply (must be between 0 and 100).
 */
public record UpdateQuoteResource(
        @NotNull(message = "billing.error.resource.discount.required")
        @Min(value = 0, message = "billing.error.resource.discount.min")
        @Max(value = 100, message = "billing.error.resource.discount.max") Double discountPercentage
) {}
