package com.forkdevs.driveos.platform.billing.domain.model.entities;

import com.forkdevs.driveos.platform.billing.domain.model.valueobjects.PaymentMethod;
import com.forkdevs.driveos.platform.shared.domain.model.valueobjects.Money;
import lombok.Getter;

import java.util.UUID;

/**
 * Entity representing a monetary payment made against a {@link com.forkdevs.driveos.platform.billing.domain.model.aggregates.Voucher}.
 * Payments track the amount paid and the payment method used.
 */
@Getter
public class Payment {
    private UUID id;
    private Money amount;
    private PaymentMethod method;
    private UUID branchId;

    /**
     * Default constructor required for persistence frameworks.
     */
    public Payment() {
        // Required for persistence
    }

    /**
     * Creates a new Payment and validates its core invariants.
     * 
     * @param amount the monetary amount of the payment (must be strictly positive)
     * @param method the payment method used
     * @param branchId the ID of the branch where the payment is received
     * @throws IllegalArgumentException if amount is zero/negative, or if method/branchId are null
     */
    public Payment(Money amount, PaymentMethod method, UUID branchId) {
        if (amount == null || amount.amount().compareTo(java.math.BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("billing.error.payment.invalidAmount");
        }
        if (method == null) {
            throw new IllegalArgumentException("billing.error.payment.methodRequired");
        }
        if (branchId == null) {
            throw new IllegalArgumentException("billing.error.payment.branchIdRequired");
        }
        
        this.id = UUID.randomUUID();
        this.amount = amount;
        this.method = method;
        this.branchId = branchId;
    }

    // For persistence rebuilding
    public Payment(UUID id, Money amount, PaymentMethod method, UUID branchId) {
        this.id = id;
        this.amount = amount;
        this.method = method;
        this.branchId = branchId;
    }
}
