package com.forkdevs.driveos.platform.billing.domain.model.commands;

import com.forkdevs.driveos.platform.billing.domain.model.valueobjects.VoucherType;

import java.util.UUID;

public record GenerateVoucherCommand(
        UUID quoteId,
        VoucherType type,
        String customerDocumentType,
        String customerDocumentNumber,
        String customerName
) {
}
