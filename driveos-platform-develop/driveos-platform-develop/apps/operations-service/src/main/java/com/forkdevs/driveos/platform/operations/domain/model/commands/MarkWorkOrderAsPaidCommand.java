package com.forkdevs.driveos.platform.operations.domain.model.commands;

import com.forkdevs.driveos.platform.operations.domain.model.valueobjects.WorkOrderId;

/**
 * Command representing the intent to mark a Work Order as paid
 * Resides inside the domain commands package
 * @param workOrderId The unique identifier of the Work Order
 * @author Joel Huamani Estefanero
 */
public record MarkWorkOrderAsPaidCommand(WorkOrderId workOrderId) {}
