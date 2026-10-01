package com.forkdevs.driveos.platform.operations.domain.model.aggregates;

import com.forkdevs.driveos.platform.operations.domain.model.entities.WorkOrderTask;
import com.forkdevs.driveos.platform.operations.domain.model.entities.WorkOrderTaskProduct;
import com.forkdevs.driveos.platform.operations.domain.model.events.ProductReservationCanceledEvent;
import com.forkdevs.driveos.platform.operations.domain.model.events.ProductReservedEvent;
import com.forkdevs.driveos.platform.operations.domain.model.events.WorkOrderPaidEvent;
import com.forkdevs.driveos.platform.operations.domain.model.valueobjects.*;
import com.forkdevs.driveos.platform.shared.domain.model.valueobjects.*;
import com.forkdevs.driveos.platform.shared.domain.model.aggregates.AbstractDomainAggregateRoot;

import lombok.Getter;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * Aggregate root representing a Work Order in the automotive service center context. This class encapsulates all the business logic related to managing a Work Order, including adding/removing tasks and products, updating details, and handling state transitions. It also integrates with Spring Data's domain events to publish relevant events when certain actions are performed, such as reserving products or marking the order as paid.
 * The WorkOrder entity is designed to be persisted in a relational database using JPA, with appropriate annotations for entity mapping, auditing, and soft deletion. It maintains a list of associated WorkOrderTask entities, which represent the individual tasks that need to be performed as part of the work order.
 * @author Joel Huamani Estefanero
 */
@Getter
public class WorkOrder extends AbstractDomainAggregateRoot<WorkOrder> {

    private WorkOrderId id;
    private AppointmentId appointmentId;
    private BranchId branchId;
    private VehicleId vehicleId;
    private CustomerId customerId;
    private Integer internalNumber;
    private WorkOrderStatus status;
    private DiagnosticSummary diagnosticSummary;
    private Mileage mileageIn;
    private Money totalAmount;
    private List<WorkOrderTask> tasks;
    private Instant createdAt;
    private Instant updatedAt;
    private Instant deletedAt;
    private UUID createdBy;
    private UUID updatedBy;
    private Long version;

    public WorkOrder() {}

    public WorkOrder(WorkOrderId id, AppointmentId appointmentId, BranchId branchId, VehicleId vehicleId, CustomerId customerId, Integer internalNumber, WorkOrderStatus status, DiagnosticSummary diagnosticSummary, Mileage mileageIn, Money totalAmount, List<WorkOrderTask> tasks, Instant createdAt, Instant updatedAt, Instant deletedAt, UUID createdBy, UUID updatedBy, Long version) {
        this.id = id;
        this.appointmentId = appointmentId;
        this.branchId = branchId;
        this.vehicleId = vehicleId;
        this.customerId = customerId;
        this.internalNumber = internalNumber;
        this.status = status;
        this.diagnosticSummary = diagnosticSummary;
        this.mileageIn = mileageIn;
        this.totalAmount = totalAmount;
        this.tasks = tasks;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
        this.deletedAt = deletedAt;
        this.createdBy = createdBy;
        this.updatedBy = updatedBy;
        this.version = version;
    }

    /**
     * Public constructor for creating a new WorkOrder instance. This constructor initializes the WorkOrder with the provided details and sets the initial status to PENDING. It also generates a new UUID for the WorkOrder ID and initializes the total amount to zero.
     * @param appointmentId the unique identifier of the associated appointment for this work order
     * @param branchId the unique identifier of the branch where the work order is being created
     * @param vehicleId the unique identifier of the vehicle associated with this work order
     * @param customerId the unique identifier of the customer who owns the vehicle and for whom the work order is being created
     * @param internalNumber the internal number assigned to this work order for tracking purposes within the branch
     * @param diagnosticSummary a summary of the diagnostic findings for the vehicle, which provides context for the tasks that will be performed as part of this work order
     * @param mileageIn the mileage of the vehicle at the time it was brought in for service, which can be relevant for certain types of maintenance tasks and for tracking the vehicle's service history
     */
    public WorkOrder(AppointmentId appointmentId, BranchId branchId, VehicleId vehicleId, CustomerId customerId, Integer internalNumber, DiagnosticSummary diagnosticSummary, Mileage mileageIn) {
        this.id = new WorkOrderId(UUID.randomUUID());
        this.appointmentId = appointmentId;
        this.branchId = branchId;
        this.vehicleId = vehicleId;
        this.customerId = customerId;
        this.internalNumber = internalNumber;
        this.diagnosticSummary = diagnosticSummary;
        this.mileageIn = mileageIn;
        this.status = WorkOrderStatus.PENDING;
        this.totalAmount = Money.ZERO;
        this.tasks = new ArrayList<>();
    }

    /**
     * Adds a new task to the work order. This method checks if the work order is in a state that allows modifications (not COMPLETED or PAID) before adding the task. It creates a new WorkOrderTask instance with the provided details and adds it to the list of tasks associated with this work order. After adding the task, it recalculates the total amount for the work order to reflect the addition of the new task.
     *
     * @param serviceId   the unique identifier of the service that this task represents, which corresponds to a specific type of work that needs to be performed on the vehicle
     * @param mechanicId  the unique identifier of the mechanic assigned to perform this task, which allows for tracking who is responsible for each task and can be used for scheduling and workload management
     * @param description a textual description of the task, providing details about what needs to be done and any specific instructions or notes for the mechanic
     * @throws IllegalStateException if the work order is in a state that does not allow modifications (COMPLETED or PAID), which prevents adding tasks to a closed order
     */
    public void addTask(ServiceId serviceId, MechanicId mechanicId, TaskDescription description, Money laborPrice) {
        if (this.status == WorkOrderStatus.COMPLETED || this.status == WorkOrderStatus.PAID) {
            throw new IllegalStateException("operations.error.workOrder.cannotModifyClosedOrder");
        }
        WorkOrderTask task = new WorkOrderTask(serviceId, this.branchId, mechanicId, description, laborPrice);
        this.tasks.add(task);
        recalculateTotalAmount();
    }

    /**
     * Adds a product to a specific task within the work order. This method first checks if the work order is in a state that allows modifications (not COMPLETED or PAID). It then finds the specified task by its ID and adds the product to that task using the provided details. After adding the product, it recalculates the total amount for the work order to reflect the addition of the new product. Additionally, it registers a ProductReservedEvent to notify the inventory system to reserve the quantity of the product that was added to the task.
     *
     * @param taskId    the unique identifier of the task to which the product should be added, which allows for associating the product with the correct task within the work order
     * @param productId the unique identifier of the product being added, which corresponds to a specific item that is required for the task and can be used for inventory management and billing purposes
     * @param quantity  the quantity of the product being added, which indicates how many units of the product are needed for the task and can affect inventory levels and the total amount of the work order
     * @throws IllegalStateException if the work order is in a state that does not allow modifications (COMPLETED or PAID), which prevents adding products to tasks of a closed order
     */
    public void addProductToTask(WorkOrderTaskId taskId, ProductId productId, Quantity quantity, Money unitPrice) {
        if (this.status == WorkOrderStatus.COMPLETED || this.status == WorkOrderStatus.PAID) {
            throw new IllegalStateException("operations.error.workOrder.cannotModifyClosedOrder");
        }
        WorkOrderTask task = findTaskOrThrow(taskId);
        task.addProduct(productId, quantity, unitPrice);
        recalculateTotalAmount();
        this.registerEvent(new ProductReservedEvent(this, this.branchId, productId, quantity));
    }

    /**
     * Removes a product from a specific task within the work order. This method first checks if the work order is in a state that allows modifications (not COMPLETED or PAID). It then finds the specified task by its ID and locates the product within that task. If the product is found and is not already marked as deleted, it removes the product from the task and recalculates the total amount for the work order to reflect the removal. Additionally, it registers a ProductReservationCanceledEvent to notify the inventory system to release the quantity of the product that was removed from the task back into available stock.
     * @param taskId the unique identifier of the task from which the product should be removed, which allows for identifying the correct task within the work order to modify
     * @param productId the unique identifier of the product being removed, which corresponds to a specific item that was previously added to the task and needs to be removed for inventory management and billing purposes
     * @throws IllegalStateException if the work order is in a state that does not allow modifications (COMPLETED or PAID), which prevents removing products from tasks of a closed order
     * @throws IllegalArgumentException if the specified product is not found within the task or is already marked as deleted, which prevents removing a product that does not exist or has already been removed from the task
     */
    public void removeProductFromTask(WorkOrderTaskId taskId, ProductId productId) {
        if (this.status == WorkOrderStatus.COMPLETED || this.status == WorkOrderStatus.PAID) {
            throw new IllegalStateException("operations.error.workOrder.cannotModifyClosedOrder");
        }
        WorkOrderTask task = findTaskOrThrow(taskId);

        WorkOrderTaskProduct product = task.getProducts().stream()
                .filter(p -> p.getProductId().equals(productId) && !p.isDeleted())
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("operations.error.taskProduct.notFound"));

        Quantity returnedQuantity = product.getQuantity();
        task.removeProduct(productId);
        recalculateTotalAmount();

        this.registerEvent(new ProductReservationCanceledEvent(this, this.branchId, productId, returnedQuantity));
    }

    /**
     * Removes a task from the work order. This method first checks if the work order is in a state that allows modifications (not COMPLETED or PAID). It then finds the specified task by its ID and checks if it is not already marked as deleted. If the task is active, it iterates through all the products associated with that task and registers a ProductReservationCanceledEvent for each active product to release their reserved quantities back into available stock. Finally, it removes the task from the list of tasks and recalculates the total amount for the work order to reflect the removal of the task and its associated products.
     * @param taskId the unique identifier of the task to be removed, which allows for identifying the correct task within the work order to delete
     * @throws IllegalStateException if the work order is in a state that does not allow modifications (COMPLETED or PAID), which prevents deleting tasks of a closed order
     * @throws IllegalStateException if the task to be removed is already marked as completed, which prevents deleting tasks that have already been finished and may have implications for record-keeping and accountability
     */
    public void removeTask(WorkOrderTaskId taskId) {
        if (this.status == WorkOrderStatus.COMPLETED || this.status == WorkOrderStatus.PAID) {
            throw new IllegalStateException("operations.error.workOrder.cannotModifyClosedOrder");
        }
        WorkOrderTask task = findTaskOrThrow(taskId);
        if (task.getStatus() == WorkOrderTaskStatus.COMPLETED) {
            throw new IllegalStateException("operations.error.workOrder.cannotDeleteCompletedTask");
        }

        for (WorkOrderTaskProduct product : task.getProducts()) {
            if (!product.isDeleted()) {
                this.registerEvent(new ProductReservationCanceledEvent(this, this.branchId, product.getProductId(), product.getQuantity()));
            }
        }
        this.tasks.remove(task);
        recalculateTotalAmount();
    }

    /**
     * Marks the work order as deleted by setting the deletedAt timestamp. This method first checks if the work order is in a state that allows deletion (not PAID). If the work order is paid, it throws an exception to prevent deletion. If the work order can be deleted, it sets the deletedAt timestamp to the current time and iterates through all tasks and their associated products to register ProductReservationCanceledEvent for each active product, ensuring that any reserved stock is released back into available inventory.
     * @throws IllegalStateException if the work order is in a state that does not allow deletion (PAID), which prevents deleting orders that have already been paid for and may have financial implications
     */
    public void delete() {
        if (this.status == WorkOrderStatus.PAID) {
            throw new IllegalStateException("operations.error.workOrder.cannotDeletePaidOrder");
        }
        this.deletedAt = Instant.now();
        for (WorkOrderTask task : this.tasks) {
            if (!task.isDeleted()) {
                for (WorkOrderTaskProduct product : task.getProducts()) {
                    if (!product.isDeleted()) {
                        this.registerEvent(new ProductReservationCanceledEvent(this, this.branchId, product.getProductId(), product.getQuantity()));
                    }
                }
            }
        }
    }

    /**
     * Starts a specific task within the work order. This method first checks if the work order is in a state that allows modifications (not COMPLETED or PAID). It then finds the specified task by its ID and calls the start() method on that task, which changes its status to IN_PROGRESS. After starting the task, it calls checkAutoCompletion() to automatically transition the work order to IN_PROGRESS if it was previously in PENDING status, ensuring that the overall work order status accurately reflects the progress of its tasks.
     * @param taskId the unique identifier of the task to be started, which allows for identifying the correct task within the work order to update its status
     * @throws IllegalStateException if the work order is in a state that does not allow modifications (COMPLETED or PAID), which prevents starting tasks of a closed order
     */
    public void startTask(WorkOrderTaskId taskId) {
        if (this.status == WorkOrderStatus.COMPLETED || this.status == WorkOrderStatus.PAID) {
            throw new IllegalStateException("operations.error.workOrder.cannotModifyClosedOrder");
        }

        WorkOrderTask task = findTaskOrThrow(taskId);
        task.start();

        checkAutoCompletion();
    }

    /**
     * Marks a specific task within the work order as completed. This method first checks if the work order is in a state that allows modifications (not COMPLETED or PAID). It then finds the specified task by its ID and calls the complete() method on that task, which changes its status to COMPLETED. After completing the task, it checks if all active tasks within the work order are now completed. If all tasks are completed, it automatically transitions the overall work order status to COMPLETED. If not all tasks are completed, it calls checkAutoCompletion() to ensure that the work order status is updated to IN_PROGRESS if it was previously in PENDING status.
     * @param taskId the unique identifier of the task to be marked as completed, which allows for identifying the correct task within the work order to update its status
     */
    public void completeTask(WorkOrderTaskId taskId) {
        WorkOrderTask task = findTaskOrThrow(taskId);
        if (task.complete()) {
            boolean allTasksCompleted = this.tasks.stream()
                    .filter(t -> !t.isDeleted())
                    .allMatch(t -> t.getStatus() == WorkOrderTaskStatus.COMPLETED);

            if (allTasksCompleted) {
                this.status = WorkOrderStatus.COMPLETED;
            } else {
                checkAutoCompletion();
            }
        }
    }

    /**
     * Reopens a specific task within the work order. This method first checks if the work order is in a state that allows modifications (not PAID). It then finds the specified task by its ID and calls the reopen() method on that task, which changes its status back to IN_PROGRESS if it was previously COMPLETED. After reopening the task, if the overall work order status was COMPLETED, it transitions it back to IN_PROGRESS to reflect that there is now an active task that needs attention.
     * @param taskId the unique identifier of the task to be reopened, which allows for identifying the correct task within the work order to update its status
     * @throws IllegalStateException if the work order is in a state that does not allow modifications (PAID), which prevents reopening tasks of a paid order
     */
    public void reopenTask(WorkOrderTaskId taskId) {
        if (this.status == WorkOrderStatus.PAID) {
            throw new IllegalStateException("operations.error.workOrder.cannotReopenTaskOfPaidOrder");
        }
        WorkOrderTask task = findTaskOrThrow(taskId);
        if (task.reopen()) {
            if (this.status == WorkOrderStatus.COMPLETED) {
                this.status = WorkOrderStatus.IN_PROGRESS;
            }
        }
    }

    /**
     * Starts the work order by transitioning its status to IN_PROGRESS. This method first checks if the work order is in a state that allows modifications (not COMPLETED or PAID). If the work order is in a valid state, it transitions the status from PENDING to IN_PROGRESS. If the work order is already in IN_PROGRESS status, this method will have no effect. This method is typically called when the first task of the work order is started, ensuring that the overall work order status accurately reflects that work has begun.
     */
    public void startWork() {
        this.status = this.status.transitionTo(WorkOrderStatus.IN_PROGRESS);
    }

    /**
     * Completes the work order by transitioning its status to COMPLETED. This method first checks if all active tasks within the work order are completed. If there are any tasks that are not completed, it throws an exception to prevent marking the work order as completed while there are still pending tasks. If all tasks are completed, it transitions the overall work order status to COMPLETED. This method is typically called when the last task of the work order is completed, ensuring that the overall work order status accurately reflects that all work has been finished.
     * @throws IllegalStateException if there are any active tasks within the work order that are not completed, which prevents marking the work order as completed while there are still pending tasks that need to be addressed
     */
    public void completeWorkOrder() {
        boolean allTasksCompleted = this.tasks.stream()
                .allMatch(t -> t.getStatus() == WorkOrderTaskStatus.COMPLETED);
        if (!allTasksCompleted) {
            throw new IllegalStateException("operations.error.workOrder.pendingTasksExist");
        }
        this.status = this.status.transitionTo(WorkOrderStatus.COMPLETED);
    }

    /**
     * Marks the work order as paid by transitioning its status to PAID. This method first checks if the work order is in a state that allows modifications (not already PAID). If the work order is already marked as PAID, it throws an exception to prevent marking it as paid multiple times. If the work order is in a valid state, it transitions the status to PAID and registers a WorkOrderPaidEvent, which includes details about the dispatched products for inventory management and billing purposes.
     */
    public void markAsPaid() {
        this.status = this.status.transitionTo(WorkOrderStatus.PAID);

        List<WorkOrderTaskProduct> dispatchedProducts = new ArrayList<>();
        for (WorkOrderTask task : this.tasks) {
            if (!task.isDeleted()) {
                for (WorkOrderTaskProduct product : task.getProducts()) {
                    if (!product.isDeleted()) {
                        dispatchedProducts.add(product);
                    }
                }
            }
        }
        this.registerEvent(new WorkOrderPaidEvent(this, this.branchId, dispatchedProducts));
    }

    /**
     * Recalculates the total amount for the work order by summing up the prices of all active tasks and their associated products. This method iterates through the list of tasks, retrieves the price for each task (which includes the service price and the total price of its products), and sums them up to calculate the overall total amount for the work order. This method is called whenever there are changes to tasks or products that could affect the total cost, ensuring that the total amount remains accurate and up-to-date.
     */
    private void recalculateTotalAmount() {
        this.totalAmount = this.tasks.stream()
                .map(WorkOrderTask::getPrice)
                .reduce(Money.ZERO, Money::plus);
    }

    /**
     * Finds a task within the work order by its unique identifier. This method searches through the list of tasks associated with the work order to find a task that matches the provided taskId. If a matching task is found, it is returned. If no matching task is found, an IllegalArgumentException is thrown with a specific error message indicating that the task was not found. This method is used internally by other methods that need to perform operations on specific tasks, ensuring that they can reliably locate the correct task based on its ID.
     * @param taskId the unique identifier of the task to be found, which allows for identifying the correct task within the work order to perform operations on it
     * @return the WorkOrderTask instance that matches the provided taskId
     * @throws IllegalArgumentException if no task with the provided taskId is found within the
     */
    private WorkOrderTask findTaskOrThrow(WorkOrderTaskId taskId) {
        return this.tasks.stream()
                .filter(t -> t.getId().equals(taskId))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("operations.error.task.notFound"));
    }

    /**
     * Updates the diagnostic summary and mileage information for the work order. This method first checks if the work order is in a state that allows modifications (not COMPLETED or PAID). If the work order is in a valid state, it updates the diagnosticSummary and mileageIn fields with the provided values. This method allows for updating important contextual information about the vehicle and its condition, which can be relevant for mechanics and for tracking the service history of the vehicle.
     * @param diagnosticSummary the new diagnostic summary to be set for the work order, which provides updated information about the findings from the vehicle inspection and can guide the tasks that need to be performed
     * @param mileageIn the new mileage information to be set for the work order, which indicates the updated mileage of the vehicle at the time of service and can be relevant for maintenance scheduling and service history tracking
     * @throws IllegalStateException if the work order is in a state that does not allow modifications (COMPLETED or PAID), which prevents changes to critical information after the work order has been finalized
     */
    public void updateDetails(DiagnosticSummary diagnosticSummary, Mileage mileageIn) {
        if (this.status == WorkOrderStatus.COMPLETED || this.status == WorkOrderStatus.PAID) {
            throw new IllegalStateException("operations.error.workOrder.cannotModifyClosedOrder");
        }
        this.diagnosticSummary = diagnosticSummary;
        this.mileageIn = mileageIn;
    }

    /**
     * Updates the details of a specific task within the work order. This method first checks if the work order is in a state that allows modifications (not COMPLETED or PAID). It then finds the specified task by its ID and calls the updateDetails() method on that task, passing in the new serviceId, mechanicId, description, and price. After updating the task details, it recalculates the total amount for the work order to reflect any changes in the price or other cost-related details of the task.
     *
     * @param taskId      the unique identifier of the task to be updated, which allows for identifying the correct task within the work order to modify its details
     * @param serviceId   the new service ID to be set for the task, which corresponds to a specific type of work that needs to be performed and can affect the service price and overall cost of the task
     * @param mechanicId  the new mechanic ID to be set for the task, which allows for tracking who is responsible for the task and can be used for scheduling and workload management
     * @param description the new description to be set for the task, providing updated details about what needs to be done and any specific instructions or notes for the mechanic
     * @throws IllegalStateException if the work order is in a state that does not allow
     */
    public void updateTaskDetails(WorkOrderTaskId taskId, ServiceId serviceId, MechanicId mechanicId, TaskDescription description, Money newLaborPrice) {
        if (this.status == WorkOrderStatus.COMPLETED || this.status == WorkOrderStatus.PAID) {
            throw new IllegalStateException("operations.error.workOrder.cannotModifyClosedOrder");
        }
        WorkOrderTask task = findTaskOrThrow(taskId);
        task.updateDetails(serviceId, mechanicId, description, newLaborPrice);
        recalculateTotalAmount();
    }

    /**
     * Updates the quantity of a specific product within a task in the work order. This method first checks if the work order is in a state that allows modifications (not COMPLETED or PAID). It then finds the specified task by its ID and calls the updateProductQuantity() method on that task, passing in the productId and the new quantity. After updating the product quantity, it recalculates the total amount for the work order to reflect any changes in the cost of the products. Additionally, it calculates the difference (delta) between the new quantity and the old quantity to determine whether to register a ProductReservedEvent (if the quantity increased) or a ProductReservationCanceledEvent (if the quantity decreased), ensuring that inventory reservations are accurately updated based on the changes made to the product quantity.
     * @param taskId the unique identifier of the task that contains the product to be updated, which allows for identifying the correct task within the work order to modify the product quantity
     * @param productId the unique identifier of the product whose quantity is being updated, which corresponds to a specific item that is required for the task and can affect inventory management and billing purposes
     * @param newQuantity the new quantity to be set for the product, which indicates how many units of the product are needed for the task and can affect inventory levels and the total amount of the work order
     * @throws IllegalStateException if the work order is in a state that does not allow modifications (COMPLETED or PAID), which prevents changes to the product quantity after the work order has been finalized
     */
    public void updateProductQuantityInTask(WorkOrderTaskId taskId, ProductId productId, Quantity newQuantity) {
        if (this.status == WorkOrderStatus.COMPLETED || this.status == WorkOrderStatus.PAID) {
            throw new IllegalStateException("operations.error.workOrder.cannotModifyClosedOrder");
        }
        WorkOrderTask task = findTaskOrThrow(taskId);

        Quantity oldQuantity = task.updateProductQuantity(productId, newQuantity);
        recalculateTotalAmount();

        int delta = newQuantity.value() - oldQuantity.value();
        if (delta > 0) {
            this.registerEvent(new ProductReservedEvent(this, this.branchId, productId, new Quantity(delta)));
        } else if (delta < 0) {
            this.registerEvent(new ProductReservationCanceledEvent(this, this.branchId, productId, new Quantity(Math.abs(delta))));
        }
    }

    /**
     * Checks if the work order should automatically transition to IN_PROGRESS status. This method is called after starting or completing tasks to ensure that the overall work order status accurately reflects the progress of its tasks. If the current status of the work order is PENDING and a task has been started, it transitions the work order status to IN_PROGRESS. This allows for automatic state management of the work order based on the actions performed on its tasks, ensuring that the status remains consistent with the actual progress of the work being done.
     */
    private void checkAutoCompletion() {
        if (this.status == WorkOrderStatus.PENDING) {
            this.status = WorkOrderStatus.IN_PROGRESS;
        }
    }
}
