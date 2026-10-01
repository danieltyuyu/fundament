package com.forkdevs.driveos.platform.billing.infrastructure.outbound.facthub;

import java.math.BigDecimal;
import java.util.List;

public record FacthubIssueInvoiceRequest(
        String issuerRuc,
        String documentType,
        String customerDocumentType,
        String customerDocumentNumber,
        String customerName,
        List<Item> items
) {
    public record Item(
            String description,
            Integer quantity,
            BigDecimal unitPrice
    ) {}
}
