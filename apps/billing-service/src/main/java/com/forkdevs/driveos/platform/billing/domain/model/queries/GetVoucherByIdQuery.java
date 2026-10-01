package com.forkdevs.driveos.platform.billing.domain.model.queries;

import java.util.UUID;

/**
 * Query to retrieve a Voucher by its unique identifier.
 * 
 * @param voucherId the unique identifier of the voucher
 */
public record GetVoucherByIdQuery(UUID voucherId) {
    public GetVoucherByIdQuery {
        if (voucherId == null) {
            throw new IllegalArgumentException("billing.error.query.voucherIdRequired");
        }
    }
}
