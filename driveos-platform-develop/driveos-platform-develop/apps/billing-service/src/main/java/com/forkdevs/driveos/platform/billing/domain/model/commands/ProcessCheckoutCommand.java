package com.forkdevs.driveos.platform.billing.domain.model.commands;

import com.forkdevs.driveos.platform.billing.domain.model.valueobjects.PaymentMethod;
import com.forkdevs.driveos.platform.billing.domain.model.valueobjects.VoucherType;

import java.util.UUID;

public record ProcessCheckoutCommand(
        UUID quoteId,
        VoucherType type,
        String customerDocumentType,
        String customerDocumentNumber,
        String customerName,
        PaymentMethod method
) {
    public ProcessCheckoutCommand {
        if (quoteId == null) {
            throw new IllegalArgumentException("billing.error.command.quoteIdRequired");
        }
        if (type == null) {
            throw new IllegalArgumentException("billing.error.command.typeRequired");
        }
        if (customerDocumentType == null || customerDocumentType.isBlank()) {
            throw new IllegalArgumentException("billing.error.command.customerDocumentTypeRequired");
        }
        if (customerDocumentNumber == null || customerDocumentNumber.isBlank()) {
            throw new IllegalArgumentException("billing.error.command.customerDocumentNumberRequired");
        }
        if (customerName == null || customerName.isBlank()) {
            throw new IllegalArgumentException("billing.error.command.customerNameRequired");
        }
        if (method == null) {
            throw new IllegalArgumentException("billing.error.command.paymentMethodRequired");
        }
    }
}
