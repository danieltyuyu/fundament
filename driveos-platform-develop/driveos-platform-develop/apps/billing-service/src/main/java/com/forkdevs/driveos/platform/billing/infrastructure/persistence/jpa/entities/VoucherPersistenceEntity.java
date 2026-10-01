package com.forkdevs.driveos.platform.billing.infrastructure.persistence.jpa.entities;

import com.forkdevs.driveos.platform.billing.domain.model.valueobjects.VoucherStatus;
import com.forkdevs.driveos.platform.billing.domain.model.valueobjects.VoucherType;
import com.forkdevs.driveos.platform.shared.infrastructure.persistence.jpa.entities.AuditableAbstractPersistenceEntity;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.UUID;

/**
 * JPA entity mapping for the Voucher aggregate.
 * Stores billing document data (Invoices/Receipts) and maintains a one-to-many 
 * relationship with its associated payments.
 */
@Entity
@Table(name = "vouchers")
@Getter
@Setter
public class VoucherPersistenceEntity extends AuditableAbstractPersistenceEntity {

    @Column(name = "quote_id", nullable = false)
    private UUID quoteId;

    @Enumerated(EnumType.STRING)
    @Column(name = "type", nullable = false, length = 20)
    private VoucherType type;

    @Column(name = "customer_document_type", nullable = false, length = 20)
    private String customerDocumentType;

    @Column(name = "customer_document_number", nullable = false, length = 20)
    private String customerDocumentNumber;

    @Column(name = "customer_name", nullable = false, length = 150)
    private String customerName;

    @Column(name = "total_amount", nullable = false, precision = 10, scale = 2)
    private BigDecimal totalAmount;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false, length = 20)
    private VoucherStatus status;

    @Column(name = "external_invoice_id", nullable = false)
    private UUID externalInvoiceId;

    @OneToMany(mappedBy = "voucher", cascade = CascadeType.ALL, orphanRemoval = true)
    private java.util.List<PaymentPersistenceEntity> payments = new java.util.ArrayList<>();
}
