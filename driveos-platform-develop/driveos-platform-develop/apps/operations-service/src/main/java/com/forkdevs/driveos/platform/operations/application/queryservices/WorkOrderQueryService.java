package com.forkdevs.driveos.platform.operations.application.queryservices;

import com.forkdevs.driveos.platform.operations.domain.model.aggregates.WorkOrder;
import com.forkdevs.driveos.platform.operations.domain.model.queries.*;
import java.util.List;
import java.util.Optional;

/**
 * Application service contract providing read access to Work Orders.
 * Exposes query operations used by the interface layer to retrieve data without exposing persistence details.
 * @author Joel Huamani Estefanero
 */
public interface WorkOrderQueryService {

    /**
     * Handles the retrieval of a Work Order by its unique identifier. This method takes a GetWorkOrderByIdQuery as input and returns an Optional containing the Work Order if found, or an empty Optional if not found.
     * @param query The query object containing the unique identifier of the Work Order to be retrieved.
     * @return An Optional containing the Work Order if found, or an empty Optional if not found.
     */
    Optional<WorkOrder> handle(GetWorkOrderByIdQuery query);

    /**
     * Handles the retrieval of a Work Order by one of its task's unique identifier.
     * @param query The query object containing the unique identifier of the task.
     * @return An Optional containing the Work Order if found, or an empty Optional if not found.
     */
    Optional<WorkOrder> handle(GetWorkOrderByTaskIdQuery query);

    /**
     * Handles the retrieval of all Work Orders associated with a specific Branch ID. This method takes a GetWorkOrdersByBranchIdQuery as input and returns a list of Work Orders that are associated with the given Branch ID.
     * @param query The query object containing the Branch ID for which to retrieve the associated Work Orders.
     * @return A list of Work Orders associated with the specified Branch ID.
     */
    List<WorkOrder> handle(GetWorkOrdersByBranchIdQuery query);

    /**
     * Handles the retrieval of all Work Orders associated with a specific Vehicle ID. This method takes a GetWorkOrdersByVehicleIdQuery as input and returns a list of Work Orders that are associated with the given Vehicle ID.
     * @param query The query object containing the Vehicle ID for which to retrieve the associated Work Orders.
     * @return A list of Work Orders associated with the specified Vehicle ID.
     */
    List<WorkOrder> handle(GetWorkOrdersByVehicleIdQuery query);

    /**
     * Gets the short code for a branch.
     * @param branchId the unique identifier of the branch
     * @return the short code, or "WO" as fallback
     */
    String getBranchCode(java.util.UUID branchId);
}
