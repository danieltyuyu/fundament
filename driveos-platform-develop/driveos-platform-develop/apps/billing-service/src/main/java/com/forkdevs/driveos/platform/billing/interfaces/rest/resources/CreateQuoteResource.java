package com.forkdevs.driveos.platform.billing.interfaces.rest.resources;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import java.util.UUID;

/**
 * DTO representing the payload required to create a new Quote via the REST API.
 */
public record CreateQuoteResource(
        @NotNull(message = "billing.error.resource.workOrderId.required") UUID workOrderId,
        @NotNull(message = "billing.error.resource.branchId.required") UUID branchId,
        @NotNull(message = "billing.error.resource.discount.required") 
        @Min(value = 0, message = "billing.error.resource.discount.min")
        @Max(value = 100, message = "billing.error.resource.discount.max") Double discountPercentage
) {}
