package com.forkdevs.driveos.platform.operations.domain.model.queries;

import com.forkdevs.driveos.platform.operations.domain.model.valueobjects.WorkOrderTaskId;

/**
 * Query representing the intention to retrieve a work order by one of its task's unique identifier.
 * @param taskId The task ID to find the containing Work Order.
 * @author Joel Huamani Estefanero
 */
public record GetWorkOrderByTaskIdQuery(WorkOrderTaskId taskId) {}
