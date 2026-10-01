package com.forkdevs.driveos.platform.billing.interfaces.rest.resources;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.util.UUID;

public record ProcessCheckoutResource(
        @NotNull(message = "billing.error.resource.quoteId.required") UUID quoteId,
        @NotBlank(message = "billing.error.resource.type.required") String type,
        @NotBlank(message = "billing.error.resource.customerDocumentType.required") String customerDocumentType,
        @NotBlank(message = "billing.error.resource.customerDocumentNumber.required") String customerDocumentNumber,
        @NotBlank(message = "billing.error.resource.customerName.required") String customerName,
        @NotBlank(message = "billing.error.resource.method.required") String method
) {
}
