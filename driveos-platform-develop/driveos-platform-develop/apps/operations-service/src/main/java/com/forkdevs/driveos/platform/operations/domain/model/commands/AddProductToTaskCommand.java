package com.forkdevs.driveos.platform.operations.domain.model.commands;

import com.forkdevs.driveos.platform.operations.domain.model.valueobjects.*;

/**
 * Command representing the intent to add a Product to a Task within a Work Order.
 * Resides inside the domain commands package.
 * @param workOrderId
 * @param taskId
 * @param productId
 * @param quantity
 * @author Joel Huamani Estefanero
 */
public record AddProductToTaskCommand(
        WorkOrderId workOrderId,
        WorkOrderTaskId taskId,
        ProductId productId,
        Quantity quantity
) {}
