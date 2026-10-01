package com.forkdevs.driveos.platform.billing.domain.model.valueobjects;

public enum VoucherCommandFailure {
    QUOTE_NOT_FOUND,
    QUOTE_NOT_APPROVED,
    INVALID_VOUCHER_DATA,
    ISSUER_NOT_FOUND,
    FACTHUB_ISSUANCE_FAILED,
    VOUCHER_NOT_FOUND,
    VOUCHER_ALREADY_PAID,
    VOUCHER_CANCELED,
    PAYMENT_EXCEEDS_TOTAL_DEBT,
    PAYMENT_NOT_FOUND
}
