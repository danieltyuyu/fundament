package com.forkdevs.driveos.platform.billing.domain.model.events;

import java.util.UUID;

/**
 * Domain event representing the moment a voucher is fully paid.
 * 
 * @param source    The aggregate that emitted the event.
 * @param voucherId The unique identifier of the paid voucher.
 * @param quoteId   The unique identifier of the quote associated with the voucher.
 */
public record VoucherPaidEvent(Object source, UUID voucherId, UUID quoteId) {}
