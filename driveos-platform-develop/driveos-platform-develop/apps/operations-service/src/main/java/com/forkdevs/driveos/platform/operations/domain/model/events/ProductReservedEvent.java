package com.forkdevs.driveos.platform.operations.domain.model.events;

import com.forkdevs.driveos.platform.operations.domain.model.valueobjects.ProductId;
import com.forkdevs.driveos.platform.operations.domain.model.valueobjects.Quantity;
import com.forkdevs.driveos.platform.shared.domain.model.valueobjects.BranchId;

/**
 * Event representing the reservation of a Product for a Task in the operations domain. This event encapsulates all necessary information about the reservation, including the source of the event, the branch where the reservation occurred, the product being reserved, and the quantity reserved.
 * @param source
 * @param branchId
 * @param productId
 * @param quantity
 * @author Joel Huamani Estefanero
 */
public record ProductReservedEvent(
        Object source,
        BranchId branchId,
        ProductId productId,
        Quantity quantity
) {}
