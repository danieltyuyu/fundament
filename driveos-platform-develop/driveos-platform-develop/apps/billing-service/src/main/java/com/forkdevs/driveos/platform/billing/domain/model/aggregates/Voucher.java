package com.forkdevs.driveos.platform.billing.domain.model.aggregates;

import com.forkdevs.driveos.platform.billing.domain.model.valueobjects.VoucherStatus;
import com.forkdevs.driveos.platform.billing.domain.model.valueobjects.VoucherType;
import com.forkdevs.driveos.platform.shared.domain.model.aggregates.AbstractDomainAggregateRoot;
import com.forkdevs.driveos.platform.shared.domain.model.valueobjects.Money;
import lombok.Getter;

import com.forkdevs.driveos.platform.billing.domain.model.entities.Payment;
import com.forkdevs.driveos.platform.billing.domain.model.valueobjects.PaymentMethod;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import com.forkdevs.driveos.platform.billing.domain.model.events.VoucherPaidEvent;
import java.util.UUID;
/**
 * Aggregate root representing a Voucher (Invoice or Receipt) in the billing context.
 * A Voucher is generated from an APPROVED Quote and represents the financial obligation
 * of the customer to pay for the services rendered.
 */
@Getter
public class Voucher extends AbstractDomainAggregateRoot<Voucher> {

    private UUID id;
    private UUID quoteId;
    private VoucherType type;
    private String customerDocumentType;
    private String customerDocumentNumber;
    private String customerName;
    private Money totalAmount;
    private VoucherStatus status;
    private UUID externalInvoiceId; // ID returned by the Facthub service
    private List<Payment> payments = new ArrayList<>();
    /**
     * Default constructor required by the persistence assembler and JPA.
     */
    public Voucher() {
        // Required by persistence assembler
    }

    /**
     * Creates a new pending Voucher from an approved Quote.
     * Validates that the total amount is strictly greater than zero.
     * 
     * @param quoteId the unique identifier of the approved quote
     * @param type the type of voucher (e.g., RECEIPT or INVOICE)
     * @param customerDocumentType the type of the customer's document (e.g., DNI, RUC)
     * @param customerDocumentNumber the number of the customer's document
     * @param customerName the full name or legal name of the customer
     * @param totalAmount the total financial amount required to pay this voucher
     * @param externalInvoiceId the external tracking ID returned by the billing service (e.g., Facthub)
     * @throws IllegalArgumentException if the total amount is null or less than or equal to zero
     */
    public Voucher(UUID quoteId, VoucherType type, String customerDocumentType, 
                   String customerDocumentNumber, String customerName, 
                   Money totalAmount, UUID externalInvoiceId) {
        if (totalAmount == null || totalAmount.amount().compareTo(java.math.BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("billing.error.voucher.invalidTotalAmount");
        }
        
        this.id = UUID.randomUUID();
        this.quoteId = quoteId;
        this.type = type;
        this.customerDocumentType = customerDocumentType;
        this.customerDocumentNumber = customerDocumentNumber;
        this.customerName = customerName;
        this.totalAmount = totalAmount;
        this.status = VoucherStatus.PENDING;
        this.externalInvoiceId = externalInvoiceId;
    }

    // For persistence rebuilding
    public Voucher(UUID id, UUID quoteId, VoucherType type, String customerDocumentType, 
                   String customerDocumentNumber, String customerName, 
                   Money totalAmount, VoucherStatus status, UUID externalInvoiceId) {
        this.id = id;
        this.quoteId = quoteId;
        this.type = type;
        this.customerDocumentType = customerDocumentType;
        this.customerDocumentNumber = customerDocumentNumber;
        this.customerName = customerName;
        this.totalAmount = totalAmount;
        this.status = status;
        this.externalInvoiceId = externalInvoiceId;
    }

    // For persistence rebuilding with payments
    public Voucher(UUID id, UUID quoteId, VoucherType type, String customerDocumentType, 
                   String customerDocumentNumber, String customerName, 
                   Money totalAmount, VoucherStatus status, UUID externalInvoiceId, List<Payment> payments) {
        this.id = id;
        this.quoteId = quoteId;
        this.type = type;
        this.customerDocumentType = customerDocumentType;
        this.customerDocumentNumber = customerDocumentNumber;
        this.customerName = customerName;
        this.totalAmount = totalAmount;
        this.status = status;
        this.externalInvoiceId = externalInvoiceId;
        if (payments != null) {
            this.payments = payments;
        }
    }

    /**
     * Retrieves an unmodifiable view of the payments recorded against this voucher.
     * 
     * @return a list of current payments
     */
    public List<Payment> getPayments() {
        return Collections.unmodifiableList(payments);
    }

    /**
     * Calculates the total amount paid so far by summing all recorded payments.
     * 
     * @return the total amount paid as a BigDecimal
     */
    public BigDecimal getTotalPaidAmount() {
        return payments.stream()
                .map(p -> p.getAmount().amount())
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    /**
     * Records a new payment against this voucher.
     * If the total paid equals the voucher's total amount, its status transitions to PAID, 
     * and a {@link VoucherPaidEvent} domain event is registered.
     * 
     * @param amount the monetary amount of the payment
     * @param method the payment method used (e.g., CASH, CREDIT_CARD)
     * @param branchId the identifier of the branch where the payment is received
     * @throws IllegalStateException if the voucher is canceled, already paid, or if the payment exceeds the remaining debt
     */
    public void addPayment(Money amount, PaymentMethod method, UUID branchId) {
        if (this.status == VoucherStatus.CANCELED) {
            throw new IllegalStateException("billing.error.voucher.cannotAddPaymentCanceled");
        }
        if (this.status == VoucherStatus.PAID) {
            throw new IllegalStateException("billing.error.voucher.alreadyPaidInFull");
        }

        BigDecimal currentTotalPaid = getTotalPaidAmount();
        BigDecimal newTotalPaid = currentTotalPaid.add(amount.amount());

        if (newTotalPaid.compareTo(this.totalAmount.amount()) > 0) {
            throw new IllegalStateException("billing.error.voucher.paymentExceedsDebt");
        }

        this.payments.add(new Payment(amount, method, branchId));

        if (newTotalPaid.compareTo(this.totalAmount.amount()) == 0) {
            this.status = VoucherStatus.PAID;
            this.registerDomainEvent(new VoucherPaidEvent(this, this.id, this.quoteId));
        } else {
            this.status = VoucherStatus.PARTIALLY_PAID;
        }
    }

    /**
     * Removes a previously recorded payment from this voucher.
     * Recalculates the status (PENDING, PARTIALLY_PAID, or PAID) based on the remaining total paid amount.
     * 
     * @param paymentId the unique identifier of the payment to remove
     * @throws IllegalStateException if the voucher has been canceled
     * @throws IllegalArgumentException if the specified payment is not found
     */
    public void removePayment(UUID paymentId) {
        if (this.status == VoucherStatus.CANCELED) {
            throw new IllegalStateException("billing.error.voucher.cannotRemovePaymentCanceled");
        }

        Payment paymentToRemove = this.payments.stream()
                .filter(p -> p.getId().equals(paymentId))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("billing.error.payment.notFound"));

        this.payments.remove(paymentToRemove);

        BigDecimal currentTotalPaid = getTotalPaidAmount();

        if (currentTotalPaid.compareTo(BigDecimal.ZERO) == 0) {
            this.status = VoucherStatus.PENDING;
        } else if (currentTotalPaid.compareTo(this.totalAmount.amount()) < 0) {
            this.status = VoucherStatus.PARTIALLY_PAID;
        } else {
            this.status = VoucherStatus.PAID;
        }
    }
}
