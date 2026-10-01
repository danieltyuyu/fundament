package com.forkdevs.driveos.platform.operations.domain.model.events;

import com.forkdevs.driveos.platform.operations.domain.model.entities.WorkOrderTaskProduct;
import com.forkdevs.driveos.platform.shared.domain.model.valueobjects.BranchId;
import java.util.List;

/**
 * Event representing the successful payment of a Work Order. This event is typically published after a Work Order has been marked as paid, and it contains information about the branch where the payment occurred and the products that were dispatched as part of the work order. This event can be used to trigger further actions in the system, such as updating inventory levels or notifying other services about the completed transaction.
 * @param source
 * @param branchId
 * @param dispatchedProducts
 * @author Joel Huamani Estefanero
 */
public record WorkOrderPaidEvent(
        Object source,
        BranchId branchId,
        List<WorkOrderTaskProduct> dispatchedProducts
) {}
