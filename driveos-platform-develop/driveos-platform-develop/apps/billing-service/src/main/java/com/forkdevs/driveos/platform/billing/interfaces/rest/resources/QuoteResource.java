package com.forkdevs.driveos.platform.billing.interfaces.rest.resources;

import java.math.BigDecimal;
import java.util.UUID;

public record QuoteResource(
        UUID id,
        UUID workOrderId,
        UUID branchId,
        BigDecimal subtotalAmount,
        Double discountPercentage,
        BigDecimal totalAmount,
        String status
) {}
