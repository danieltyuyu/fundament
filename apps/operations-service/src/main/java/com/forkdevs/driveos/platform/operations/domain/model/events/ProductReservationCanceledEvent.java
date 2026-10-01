package com.forkdevs.driveos.platform.operations.domain.model.events;

import com.forkdevs.driveos.platform.operations.domain.model.valueobjects.ProductId;
import com.forkdevs.driveos.platform.operations.domain.model.valueobjects.Quantity;
import com.forkdevs.driveos.platform.shared.domain.model.valueobjects.BranchId;

/**
 * Event representing the cancellation of a Product reservation in the operations domain. This event encapsulates all necessary information about the canceled reservation, including the source of the event, the branch where the reservation was made, the product involved, and the quantity that was reserved.
 * @param source
 * @param branchId
 * @param productId
 * @param quantity
 * @author Joel Huamani Estefanero
 */
public record ProductReservationCanceledEvent(
        Object source,
        BranchId branchId,
        ProductId productId,
        Quantity quantity
) {}
