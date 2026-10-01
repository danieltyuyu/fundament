package com.forkdevs.driveos.platform.billing.domain.model.aggregates;

import com.forkdevs.driveos.platform.billing.domain.model.commands.CreateQuoteCommand;
import com.forkdevs.driveos.platform.billing.domain.model.valueobjects.QuoteStatus;
import com.forkdevs.driveos.platform.shared.domain.model.aggregates.AbstractDomainAggregateRoot;
import com.forkdevs.driveos.platform.shared.domain.model.valueobjects.BranchId;
import com.forkdevs.driveos.platform.shared.domain.model.valueobjects.Money;
import lombok.Getter;

import java.util.UUID;

/**
 * Aggregate root representing a Quote in the billing context.
 * This class encapsulates business logic related to quoting a Work Order before generating a voucher.
 * It tracks subtotals, applies discounts, calculates the final total amount, and manages its state transitions.
 */
@Getter
public class Quote extends AbstractDomainAggregateRoot<Quote> {

    private UUID id;
    private UUID workOrderId;
    private BranchId branchId;
    private Money subtotalAmount;
    private Double discountPercentage;
    private Money totalAmount;
    private QuoteStatus status;

    public Quote() {
        // Required by persistence assembler
    }

    public Quote(CreateQuoteCommand command, Money subtotalAmount) {
        this.id = UUID.randomUUID();
        this.workOrderId = command.workOrderId();
        this.branchId = command.branchId();
        this.subtotalAmount = subtotalAmount;
        this.discountPercentage = command.discountPercentage();
        this.status = QuoteStatus.DRAFT;
        
        calculateTotal();
    }

    /**
     * Calculates the total amount based on the subtotal and the applied discount percentage.
     * Sets the totalAmount property. If subtotal is null, totalAmount defaults to ZERO.
     */
    private void calculateTotal() {
        if (this.subtotalAmount == null) {
            this.totalAmount = Money.ZERO;
            return;
        }
        double discountFactor = 1.0 - (this.discountPercentage / 100.0);
        this.totalAmount = this.subtotalAmount.multiply(java.math.BigDecimal.valueOf(discountFactor));
    }

    /**
     * Approves the quote, transitioning its state from DRAFT to APPROVED.
     * 
     * @throws IllegalStateException if the quote is not in DRAFT status.
     */
    public void approve() {
        if (this.status != QuoteStatus.DRAFT) {
            throw new IllegalStateException("billing.error.quote.invalidStateForApproval");
        }
        this.status = QuoteStatus.APPROVED;
    }

    /**
     * Cancels the quote, transitioning its state to CANCELED.
     * 
     * @throws IllegalStateException if the quote has already been APPROVED.
     */
    public void cancel() {
        if (this.status == QuoteStatus.APPROVED) {
            throw new IllegalStateException("billing.error.quote.cannotCancelDirectly");
        }
        this.status = QuoteStatus.CANCELED;
    }
    
    public void updateDiscount(Double newDiscount) {
        if (this.status != QuoteStatus.DRAFT) {
            throw new IllegalStateException("billing.error.quote.invalidStateForUpdate");
        }
        if (newDiscount < 0 || newDiscount > 100) {
            throw new IllegalArgumentException("billing.error.quote.invalidDiscount");
        }
        this.discountPercentage = newDiscount;
        calculateTotal();
    }
    
    // For persistence rebuilding
    public Quote(UUID id, UUID workOrderId, BranchId branchId, Money subtotalAmount, Double discountPercentage, Money totalAmount, QuoteStatus status) {
        this.id = id;
        this.workOrderId = workOrderId;
        this.branchId = branchId;
        this.subtotalAmount = subtotalAmount;
        this.discountPercentage = discountPercentage;
        this.totalAmount = totalAmount;
        this.status = status;
    }
}
