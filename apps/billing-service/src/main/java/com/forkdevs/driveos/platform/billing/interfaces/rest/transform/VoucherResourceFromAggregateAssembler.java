package com.forkdevs.driveos.platform.billing.interfaces.rest.transform;

import com.forkdevs.driveos.platform.billing.domain.model.aggregates.Voucher;
import com.forkdevs.driveos.platform.billing.interfaces.rest.resources.VoucherResource;

public class VoucherResourceFromAggregateAssembler {

    public static VoucherResource toResourceFromAggregate(Voucher aggregate) {
        var paymentResources = aggregate.getPayments().stream()
                .map(payment -> new com.forkdevs.driveos.platform.billing.interfaces.rest.resources.PaymentResource(
                        payment.getId(),
                        payment.getAmount().amount(),
                        payment.getMethod().name()
                ))
                .toList();

        return new VoucherResource(
                aggregate.getId(),
                aggregate.getQuoteId(),
                aggregate.getType().name(),
                aggregate.getCustomerDocumentType(),
                aggregate.getCustomerDocumentNumber(),
                aggregate.getCustomerName(),
                aggregate.getTotalAmount().amount(),
                aggregate.getStatus().name(),
                aggregate.getExternalInvoiceId(),
                paymentResources,
                aggregate.getTotalPaidAmount()
        );
    }
}
