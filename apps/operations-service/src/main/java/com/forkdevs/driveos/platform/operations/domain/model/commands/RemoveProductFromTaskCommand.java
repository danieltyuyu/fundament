package com.forkdevs.driveos.platform.operations.domain.model.commands;

import com.forkdevs.driveos.platform.operations.domain.model.valueobjects.ProductId;
import com.forkdevs.driveos.platform.operations.domain.model.valueobjects.WorkOrderId;
import com.forkdevs.driveos.platform.operations.domain.model.valueobjects.WorkOrderTaskId;

/**
 * Command to remove a Product from a Task within a Work Order. This command encapsulates the necessary identifiers to perform the operation, including the Work Order ID, Task ID, and Product ID.
 * @param workOrderId
 * @param taskId
 * @param productId
 * @author Joel Huamani Estefanero
 */
public record RemoveProductFromTaskCommand(
        WorkOrderId workOrderId,
        WorkOrderTaskId taskId,
        ProductId productId
) {}