package com.forkdevs.driveos.platform.operations.application.commandservices;

import com.forkdevs.driveos.platform.operations.domain.model.aggregates.WorkOrder;
import com.forkdevs.driveos.platform.operations.domain.model.commands.*;
import com.forkdevs.driveos.platform.shared.application.result.Result;

/**
 * Application service contract for executing Work Order command operations (use cases).
 * Returns a Result mapping either the successfully updated aggregate or a specific business failure.
 * @author Joel Huamani Estefanero
 */
public interface WorkOrderCommandService {

    /**
     * Create a new Work Order with initial details (diagnostic and mileage) and an empty list of tasks. The order starts in the DRAFT state.
     * @param command The command object containing all necessary information to create a new Work Order.
     * @return returns a Result containing either the created Work Order or a specific failure reason if the creation process fails (e.g., invalid data, related entities not found, etc.).
     */
    Result<WorkOrder, WorkOrderCommandFailure> handle(CreateWorkOrderCommand command);

    /**
     * Add a new Task to an existing Work Order. The task is added in the PENDING state and the Work Order's total price is updated accordingly.
     * @param command The command object containing the Work Order ID and the details of the Task to be added (service, mechanic, description, labor price).
     * @return returns a Result containing either the updated Work Order with the new Task added or a specific failure reason if the operation fails (e.g., Work Order not found, invalid task details, etc.).
     */
    Result<WorkOrder, WorkOrderCommandFailure> handle(AddTaskToWorkOrderCommand command);

    /**
     * Add a Product to a specific Task within a Work Order. The product is added in the PENDING state and the Task's total price is updated accordingly.
     * @param command The command object containing the Work Order ID, Task ID, and the details of the Product to be added (product ID, quantity).
     * @return returns a Result containing either the updated Work Order with the new Product added to the specified Task or a specific failure reason if the operation fails (e.g., Work Order or Task not found, invalid product details, etc.).
     */
    Result<WorkOrder, WorkOrderCommandFailure> handle(AddProductToTaskCommand command);

    /**
     * Remove a Product from a specific Task within a Work Order. The product is removed and the Task's total price is updated accordingly.
     * @param command The command object containing the Work Order ID, Task ID, and the Product ID of the product to be removed.
     * @return returns a Result containing either the updated Work Order with the Product removed from the specified Task or a specific failure reason if the operation fails (e.g., Work Order or Task not found, product not found in task, etc.).
     */
    Result<WorkOrder, WorkOrderCommandFailure> handle(RemoveProductFromTaskCommand command);

    /**
     * Remove a Task from a Work Order. The task is removed and the Work Order's total price is updated accordingly.
     * @param command The command object containing the Work Order ID and Task ID of the task to be removed.
     * @return returns a Result containing either the updated Work Order with the Task removed or a specific failure reason if the operation fails (e.g., Work Order or Task not found, etc.).
     */
    Result<WorkOrder, WorkOrderCommandFailure> handle(RemoveTaskFromWorkOrderCommand command);

    /**
     * Start a Task within a Work Order. The task's status is updated to IN_PROGRESS and the start time is recorded.
     * @param command The command object containing the Work Order ID and Task ID of the task to be started.
     * @return returns a Result containing either the updated Work Order with the Task status changed to IN_PROGRESS or a specific failure reason if the operation fails (e.g., Work Order or Task not found, task not in a state that allows starting, etc.).
     */
    Result<WorkOrder, WorkOrderCommandFailure> handle(StartTaskCommand command);

    /**
     * Complete a Task within a Work Order. The task's status is updated to COMPLETED and the completion time is recorded.
     * @param command The command object containing the Work Order ID and Task ID of the task to be completed.
     * @return returns a Result containing either the updated Work Order with the Task status changed to COMPLETED or a specific failure reason if the operation fails (e.g., Work Order or Task not found, task not in a state that allows completion, etc.).
     */
    Result<WorkOrder, WorkOrderCommandFailure> handle(CompleteTaskCommand command);

    /**
     * Reopen a Task within a Work Order. The task's status is updated back to PENDING and the completion time is cleared.
     * @param command The command object containing the Work Order ID and Task ID of the task to be reopened.
     * @return returns a Result containing either the updated Work Order with the Task status changed back to PENDING or a specific failure reason if the operation fails (e.g., Work Order or Task not found, task not in a state that allows reopening, etc.).
     */
    Result<WorkOrder, WorkOrderCommandFailure> handle(ReopenTaskCommand command);

    /**
     * Mark a Work Order as paid. The Work Order's status is updated to PAID and the payment time is recorded.
     * @param command The command object containing the Work Order ID of the work order to be marked as paid.
     * @return returns a Result containing either the updated Work Order with the status changed to PAID or a specific failure reason if the operation fails (e.g., Work Order not found, work order not in a state that allows marking as paid, etc.).
     */
    Result<WorkOrder, WorkOrderCommandFailure> handle(MarkWorkOrderAsPaidCommand command);

    /**
     * Update the diagnostic and mileage details of a Work Order. The Work Order's details are updated accordingly.
     * @param command The command object containing the Work Order ID and the new diagnostic and mileage details to be updated.
     * @return returns a Result containing either the updated Work Order with the new details or a specific failure reason if the operation fails (e.g., Work Order not found, invalid details, etc.).
     */
    Result<WorkOrder, WorkOrderCommandFailure> handle(UpdateWorkOrderDetailsCommand command);

    /**
     * Update the description and labor price of a specific Task within a Work Order. The Task's details are updated accordingly and the Work Order's total price is recalculated.
     * @param command The command object containing the Work Order ID, Task ID, and the new details for the task (service ID, mechanic ID, description, labor price) to be updated.
     * @return returns a Result containing either the updated Work Order with the Task's new details or a specific failure reason if the operation fails (e.g., Work Order or Task not found, invalid task details, etc.).
     */
    Result<WorkOrder, WorkOrderCommandFailure> handle(UpdateWorkOrderTaskDetailsCommand command);

    /**
     * Update the quantity of a specific Product within a Task in a Work Order. The Product's quantity is updated accordingly and the Task's total price is recalculated.
     * @param command The command object containing the Work Order ID, Task ID, Product ID, and the new quantity for the product to be updated.
     * @return returns a Result containing either the updated Work Order with the Product's new quantity or a specific failure reason if the operation fails (e.g., Work Order or Task not found, product not found in task, invalid quantity, etc.).
     */
    Result<WorkOrder, WorkOrderCommandFailure> handle(UpdateProductQuantityInTaskCommand command);

    /**
     * Delete a Work Order. The Work Order is marked as deleted and is no longer active in the system.
     * @param command The command object containing the Work Order ID of the work order to be deleted.
     * @return returns a Result containing either the deleted Work Order (or a confirmation of deletion) or a specific failure reason if the operation fails (e.g., Work Order not found, work order not in a state that allows deletion, etc.).
     */
    Result<WorkOrder, WorkOrderCommandFailure> handle(DeleteWorkOrderCommand command);
}