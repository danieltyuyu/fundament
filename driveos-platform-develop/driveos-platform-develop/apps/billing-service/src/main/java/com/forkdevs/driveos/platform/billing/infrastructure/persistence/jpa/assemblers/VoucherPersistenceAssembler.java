package com.forkdevs.driveos.platform.billing.infrastructure.persistence.jpa.assemblers;

import com.forkdevs.driveos.platform.billing.domain.model.aggregates.Voucher;
import com.forkdevs.driveos.platform.billing.infrastructure.persistence.jpa.entities.VoucherPersistenceEntity;
import com.forkdevs.driveos.platform.shared.domain.model.valueobjects.Money;
import com.forkdevs.driveos.platform.billing.domain.model.entities.Payment;
import com.forkdevs.driveos.platform.billing.infrastructure.persistence.jpa.entities.PaymentPersistenceEntity;

import java.util.stream.Collectors;

public class VoucherPersistenceAssembler {

    public static VoucherPersistenceEntity toEntity(Voucher aggregate) {
        if (aggregate == null) return null;
        var entity = new VoucherPersistenceEntity();
        updateEntityFromAggregate(entity, aggregate);
        return entity;
    }

    public static void updateEntityFromAggregate(VoucherPersistenceEntity entity, Voucher aggregate) {
        if (aggregate == null || entity == null) return;
        
        // ID is managed by JPA for new entities. We don't set it manually to avoid detached entity exceptions.
        entity.setQuoteId(aggregate.getQuoteId());
        entity.setType(aggregate.getType());
        entity.setCustomerDocumentType(aggregate.getCustomerDocumentType());
        entity.setCustomerDocumentNumber(aggregate.getCustomerDocumentNumber());
        entity.setCustomerName(aggregate.getCustomerName());
        entity.setTotalAmount(aggregate.getTotalAmount().amount());
        entity.setStatus(aggregate.getStatus());
        entity.setExternalInvoiceId(aggregate.getExternalInvoiceId());

        // Map payments
        if (aggregate.getPayments() != null) {
            var paymentEntities = aggregate.getPayments().stream().map(payment -> {
                var paymentEntity = new PaymentPersistenceEntity();
                paymentEntity.setId(payment.getId());
                paymentEntity.setAmount(payment.getAmount().amount());
                paymentEntity.setCurrency("PEN");
                paymentEntity.setMethod(payment.getMethod());
                paymentEntity.setPaidAt(java.time.LocalDateTime.now());
                paymentEntity.setBranchId(payment.getBranchId());
                paymentEntity.setVoucher(entity);
                return paymentEntity;
            }).collect(Collectors.toList());

            entity.getPayments().clear();
            entity.getPayments().addAll(paymentEntities);
        }
    }

    public static Voucher toAggregate(VoucherPersistenceEntity entity) {
        if (entity == null) return null;
        
        var payments = entity.getPayments() != null ? entity.getPayments().stream()
                .map(pEntity -> new Payment(pEntity.getId(), new Money(pEntity.getAmount()), pEntity.getMethod(), pEntity.getBranchId()))
                .collect(Collectors.toList()) : new java.util.ArrayList<Payment>();

        return new Voucher(
                entity.getId(),
                entity.getQuoteId(),
                entity.getType(),
                entity.getCustomerDocumentType(),
                entity.getCustomerDocumentNumber(),
                entity.getCustomerName(),
                new Money(entity.getTotalAmount()),
                entity.getStatus(),
                entity.getExternalInvoiceId(),
                payments
        );
    }
}
