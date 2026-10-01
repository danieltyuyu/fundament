package com.forkdevs.driveos.platform.billing.interfaces.rest.resources;

import java.math.BigDecimal;
import java.util.UUID;

public record VoucherResource(
        UUID id,
        UUID quoteId,
        String type,
        String customerDocumentType,
        String customerDocumentNumber,
        String customerName,
        BigDecimal totalAmount,
        String status,
        UUID externalInvoiceId,
        java.util.List<PaymentResource> payments,
        BigDecimal totalPaid
) {
}
