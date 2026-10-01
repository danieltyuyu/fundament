package com.forkdevs.driveos.platform.inventory.domain.model.events;

import com.forkdevs.driveos.platform.shared.domain.model.valueobjects.BranchId;

import java.util.UUID;

public record ProductReservationCanceledEvent(
        Object source,
        BranchId branchId,
        UUID productId,
        Integer quantity
) {}
