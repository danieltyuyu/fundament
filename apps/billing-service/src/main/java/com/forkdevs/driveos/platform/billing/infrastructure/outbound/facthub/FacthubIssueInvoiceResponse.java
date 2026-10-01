package com.forkdevs.driveos.platform.billing.infrastructure.outbound.facthub;

import java.math.BigDecimal;
import java.util.UUID;

public record FacthubIssueInvoiceResponse(
        boolean success,
        String message,
        InvoiceData invoice,
        SunatData sunat
) {
    public record InvoiceData(
            UUID id,
            String series,
            Integer number,
            String customerDocumentNumber,
            String customerName,
            BigDecimal totalAmount,
            String issueDate
    ) {}

    public record SunatData(
            String status,
            String ticket,
            String xmlContent
    ) {}
}
