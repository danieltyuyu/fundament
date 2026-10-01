package com.forkdevs.driveos.platform.billing.domain.model.commands;

import java.util.UUID;

public record RemovePaymentCommand(UUID voucherId, UUID paymentId) {
    public RemovePaymentCommand {
        if (voucherId == null) {
            throw new IllegalArgumentException("billing.error.command.voucherIdRequired");
        }
        if (paymentId == null) {
            throw new IllegalArgumentException("billing.error.command.paymentIdRequired");
        }
    }
}
