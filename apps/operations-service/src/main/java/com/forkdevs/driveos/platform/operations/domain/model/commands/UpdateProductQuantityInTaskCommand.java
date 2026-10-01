package com.forkdevs.driveos.platform.operations.domain.model.commands;

import com.forkdevs.driveos.platform.operations.domain.model.valueobjects.*;

/**
 * Command representing the intent to update the quantity of a Product in a Task within a Work Order.
 * @param workOrderId
 * @param taskId
 * @param productId
 * @param newQuantity
 * @author Joel Huamani Estefanero
 */
public record UpdateProductQuantityInTaskCommand(
        WorkOrderId workOrderId,
        WorkOrderTaskId taskId,
        ProductId productId,
        Quantity newQuantity
) {}
