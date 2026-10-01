package com.forkdevs.driveos.platform.operations.domain.model.commands;

import com.forkdevs.driveos.platform.operations.domain.model.valueobjects.WorkOrderId;
import com.forkdevs.driveos.platform.operations.domain.model.valueobjects.WorkOrderTaskId;

/**
 * Command to remove a Task from a Work Order. This command encapsulates the necessary information to identify the Work Order and the Task to be removed.
 * @param workOrderId
 * @param taskId
 * @author Joel Huamani Estefanero
 */
public record RemoveTaskFromWorkOrderCommand(
        WorkOrderId workOrderId,
        WorkOrderTaskId taskId
) {}
