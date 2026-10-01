package com.forkdevs.driveos.platform.operations.domain.model.commands;

import com.forkdevs.driveos.platform.operations.domain.model.valueobjects.WorkOrderId;

/**
 * Command representing the intention to delete a work order from the system. This command is typically handled by a command handler that will perform the necessary operations to remove the work order from the database and ensure that any related data is also appropriately handled.
 * @param workOrderId
 * @author Joel Huamani Estefanero
 */
public record DeleteWorkOrderCommand(WorkOrderId workOrderId) {}
