package com.forkdevs.driveos.platform.operations.domain.model.queries;

import com.forkdevs.driveos.platform.operations.domain.model.valueobjects.WorkOrderId;

/**
 * Query representing the intention to retrieve a work order by its unique identifier. This query is typically handled by a query handler that will perform the necessary operations to fetch the work order from the database and return it to the caller.
 * @param workOrderId
 * @author Joel Huamani Estefanero
 */
public record GetWorkOrderByIdQuery(WorkOrderId workOrderId) {}
