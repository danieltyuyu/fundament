package com.forkdevs.driveos.platform.billing.application.outboundservices;

import com.forkdevs.driveos.platform.billing.domain.model.valueobjects.VoucherType;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface FacthubGateway {
    
    public record FacthubItem(String description, Integer quantity, java.math.BigDecimal unitPrice) {}
    
    /**
     * Issues an electronic voucher via the Facthub Service API.
     * 
     * @param issuerRuc The RUC of the workshop issuing the voucher.
     * @param documentType The type of voucher (RECEIPT or INVOICE).
     * @param customerDocumentType The document type of the customer (e.g., DNI, RUC).
     * @param customerDocumentNumber The document number of the customer.
     * @param customerName The name of the customer.
     * @param items The items representing services and products to be invoiced.
     * @return The UUID assigned to the invoice by Facthub, or Optional.empty() if it failed.
     */
    Optional<UUID> issueVoucher(
            String issuerRuc, 
            VoucherType documentType, 
            String customerDocumentType, 
            String customerDocumentNumber, 
            String customerName, 
            List<FacthubItem> items
    );
}
