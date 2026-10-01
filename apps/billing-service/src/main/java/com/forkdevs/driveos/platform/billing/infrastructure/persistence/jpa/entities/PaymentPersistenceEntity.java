package com.forkdevs.driveos.platform.billing.infrastructure.persistence.jpa.entities;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import java.util.UUID;
import java.time.LocalDateTime;
import java.math.BigDecimal;

/**
 * JPA entity mapping for individual Payments.
 * Reflects the 'payments' table in the database. Contains a many-to-one mapping 
 * back to its parent {@link VoucherPersistenceEntity}.
 */
@Entity
@Table(name = "payments")
@Getter
@Setter
@lombok.NoArgsConstructor
public class PaymentPersistenceEntity {

    @Id
    private UUID id;

    @Column(nullable = false)
    private BigDecimal amount;

    @Column(length = 3)
    private String currency;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private com.forkdevs.driveos.platform.billing.domain.model.valueobjects.PaymentMethod method;

    @Column(name = "paid_at")
    private LocalDateTime paidAt;

    @Column(name = "branch_id")
    private UUID branchId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "voucher_id", nullable = false)
    private VoucherPersistenceEntity voucher;
}
