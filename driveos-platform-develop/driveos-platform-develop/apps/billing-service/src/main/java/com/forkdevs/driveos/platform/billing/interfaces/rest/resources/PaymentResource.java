package com.forkdevs.driveos.platform.billing.interfaces.rest.resources;

import io.swagger.v3.oas.annotations.media.Schema;

import java.math.BigDecimal;
import java.util.UUID;

@Schema(description = "Resource representing a payment made to a voucher")
public record PaymentResource(
        @Schema(description = "Payment unique identifier")
        UUID id,
        
        @Schema(description = "Amount paid")
        BigDecimal amount,
        
        @Schema(description = "Payment method used")
        String method
) {}
