package com.forkdevs.driveos.platform.billing.domain.model.valueobjects;

public enum QuoteCommandFailure {
    WORK_ORDER_NOT_FOUND,
    INVALID_QUOTE_DATA,
    QUOTE_ALREADY_EXISTS_FOR_WORK_ORDER,
    QUOTE_NOT_FOUND,
    INVALID_QUOTE_STATE
}
