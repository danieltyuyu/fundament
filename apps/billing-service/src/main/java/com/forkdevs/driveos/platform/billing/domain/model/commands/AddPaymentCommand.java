package com.forkdevs.driveos.platform.billing.domain.model.commands;

import com.forkdevs.driveos.platform.billing.domain.model.valueobjects.PaymentMethod;
import com.forkdevs.driveos.platform.shared.domain.model.valueobjects.Money;

import java.util.UUID;

public record AddPaymentCommand(UUID voucherId, Money amount, PaymentMethod method) {
    public AddPaymentCommand {
        if (voucherId == null) {
            throw new IllegalArgumentException("billing.error.command.voucherIdRequired");
        }
        if (amount == null || amount.amount().compareTo(java.math.BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("billing.error.command.paymentAmountRequired");
        }
        if (method == null) {
            throw new IllegalArgumentException("billing.error.command.paymentMethodRequired");
        }
    }
}
