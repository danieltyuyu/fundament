package com.forkdevs.driveos.platform.billing.interfaces.rest.resources;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;

@Schema(description = "Request resource to add a payment to a voucher")
public record AddPaymentResource(
        @NotNull(message = "billing.error.resource.amount.required")
        @DecimalMin(value = "0.01", message = "billing.error.resource.amount.min")
        @Schema(description = "Payment amount", example = "50.00")
        BigDecimal amount,

        @NotBlank(message = "billing.error.resource.method.required")
        @Schema(description = "Payment method (e.g. CASH, CREDIT_CARD, DEBIT_CARD, BANK_TRANSFER)", example = "CASH")
        String method
) {}
