package com.forkdevs.driveos.platform.operations.domain.model.commands;

import com.forkdevs.driveos.platform.operations.domain.model.valueobjects.WorkOrderId;
import com.forkdevs.driveos.platform.operations.domain.model.valueobjects.WorkOrderTaskId;

/**
 * Command to reopen a task within a work order. This command is used when a task that was previously closed needs to be reopened for further work or corrections.
 * It contains the identifiers for both the work order and the specific task that is to be reopened
 * @param workOrderId
 * @param taskId
 * @author Joel Huamani Estefanero
 */
public record StartTaskCommand(WorkOrderId workOrderId, WorkOrderTaskId taskId) {}
