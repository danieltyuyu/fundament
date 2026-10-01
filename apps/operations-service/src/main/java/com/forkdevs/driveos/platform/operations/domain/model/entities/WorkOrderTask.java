package com.forkdevs.driveos.platform.operations.domain.model.entities;

import com.forkdevs.driveos.platform.operations.domain.model.valueobjects.*;
import com.forkdevs.driveos.platform.shared.domain.model.valueobjects.BranchId;
import com.forkdevs.driveos.platform.shared.domain.model.valueobjects.Money;

import lombok.Getter;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * Entity representing a Work Order Task in the automotive repair shop management system. This class is an aggregate root that encapsulates the properties and behaviors of a task within a work order, including its associated products, status transitions, and audit information. It uses JPA annotations for persistence and includes logic to manage task details and enforce business rules related to task modifications based on its status.
 * @author Joel Huamani Estefanero
 */
@Getter

public class WorkOrderTask {

    private WorkOrderTaskId id;
    private ServiceId serviceId;
    private BranchId branchId;
    private MechanicId assignedMechanicId;
    private WorkOrderTaskStatus status;
    private TaskDescription description;
    private Money price;
    private Instant startedAt;
    private Instant completedAt;
    private List<WorkOrderTaskProduct> products;
    private Instant createdAt;
    private Instant updatedAt;
    private Instant deletedAt;
    private UUID createdBy;
    private UUID updatedBy;
    private Long version;

    public WorkOrderTask() {}

    public WorkOrderTask(WorkOrderTaskId id, ServiceId serviceId, BranchId branchId, MechanicId assignedMechanicId, WorkOrderTaskStatus status, TaskDescription description, Money price, Instant startedAt, Instant completedAt, List<WorkOrderTaskProduct> products, Instant createdAt, Instant updatedAt, Instant deletedAt, UUID createdBy, UUID updatedBy, Long version) {
        this.id = id;
        this.serviceId = serviceId;
        this.branchId = branchId;
        this.assignedMechanicId = assignedMechanicId;
        this.status = status;
        this.description = description;
        this.price = price;
        this.startedAt = startedAt;
        this.completedAt = completedAt;
        this.products = products;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
        this.deletedAt = deletedAt;
        this.createdBy = createdBy;
        this.updatedBy = updatedBy;
        this.version = version;
    }

    /**
     * Constructor to create a new WorkOrderTask with the specified service, branch, assigned mechanic, description, and price. It initializes the task with a unique identifier, sets the initial status to PENDING, and calculates the total price based on the price and any associated products. This constructor is used when creating a new task for a work order.
     * @param serviceId the identifier of the service associated with this task
     * @param branchId the identifier of the branch where the task will be performed
     * @param mechanicId the identifier of the mechanic assigned to this task
     * @param description a description of the task to be performed
     * @param laborPrice the price of the service associated with this task
     */
    public WorkOrderTask(ServiceId serviceId, BranchId branchId, MechanicId mechanicId, TaskDescription description, Money laborPrice) {
        this.id = new WorkOrderTaskId(UUID.randomUUID());
        this.serviceId = serviceId;
        this.branchId = branchId;
        this.assignedMechanicId = mechanicId;
        this.description = description;
        this.status = WorkOrderTaskStatus.PENDING;
        this.products = new ArrayList<>();
        this.price = laborPrice;
    }

    /**
     * Adds a product to the task or updates the quantity if the product already exists. If the task is completed, it throws an IllegalStateException to prevent modifications. If the product already exists in the task, it updates the quantity and recalculates the total price accordingly. If the product does not exist, it creates a new WorkOrderTaskProduct, adds it to the list of products, and updates the total price of the task by adding the total amount of the new product.
     * @param productId the identifier of the product to be added or updated in the task
     * @param quantity the quantity of the product to be added or updated. If the product already exists, this quantity will be added to the existing quantity.
     * @param unitPrice the unit price of the product
     */
    public void addProduct(ProductId productId, Quantity quantity, Money unitPrice) {
        if (this.status == WorkOrderTaskStatus.COMPLETED) {
            throw new IllegalStateException("operations.error.task.cannotModifyCompletedTask");
        }

        Optional<WorkOrderTaskProduct> existingProduct = this.products.stream()
                .filter(p -> p.getProductId().equals(productId) && !p.isDeleted()).findFirst();

        if (existingProduct.isPresent()) {
            WorkOrderTaskProduct product = existingProduct.get();
            Money oldTotalAmount = product.getTotalAmount();
            product.updateQuantity(new Quantity(product.getQuantity().value() + quantity.value()));
            this.price = this.price.minus(oldTotalAmount).plus(product.getTotalAmount());
        } else {
          WorkOrderTaskProduct product = new WorkOrderTaskProduct(productId, this.branchId, quantity, unitPrice);
          this.products.add(product);
          this.price = this.price.plus(product.getTotalAmount());
        }
    }

    /**
     * Removes a product from the task. If the task is completed, it throws an IllegalStateException to prevent modifications. It searches for the product in the list of products associated with the task, and if found, it marks it as deleted and updates the total price of the task by subtracting the total amount of the removed product. If the product is not found, it throws an IllegalArgumentException indicating that the product was not found in the task.
     * @param productId the identifier of the product to be removed from the task. The method will look for this product in the list of products associated with the task, and if found, it will mark it as deleted and adjust the total price of the task accordingly. If the product is not found, an exception will be thrown.
     */
    public void removeProduct(ProductId productId) {
        if (this.status == WorkOrderTaskStatus.COMPLETED) {
            throw new IllegalStateException("operations.error.task.cannotModifyCompletedTask");
        }

        WorkOrderTaskProduct product = this.products.stream()
                .filter(p -> p.getProductId().equals(productId) && !p.isDeleted())
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("operations.error.taskProduct.notFound"));

        this.price = this.price.minus(product.getTotalAmount());
        this.products.remove(product);
    }

    /**
     * Starts the task by transitioning its status to DOING and setting the startedAt timestamp to the current time. This method is used to indicate that work on the task has begun. It updates the status of the task to reflect that it is now in progress and records the time at which the task was started.
     */
    public void start() {
        this.status = this.status.transitionTo(WorkOrderTaskStatus.DOING);
        this.startedAt = Instant.now();
    }

    /**
     * Completes the task by transitioning its status to COMPLETED and setting the completedAt timestamp to the current time. This method is used to indicate that work on the task has been finished. It updates the status of the task to reflect that it is now completed and records the time at which the task was completed.
     * @return true if the task was successfully completed, false otherwise. In this implementation, it always returns true after successfully updating the status and timestamp, but in a more complex implementation, it could include additional logic to determine if the completion was successful.
     */
    public boolean complete() {
        this.status = this.status.transitionTo(WorkOrderTaskStatus.COMPLETED);
        this.completedAt = Instant.now();
        return true;
    }

    /**
     * Reopens the task by transitioning its status back to DOING and clearing the completedAt timestamp. This method is used to indicate that a previously completed task needs to be reopened for further work. It updates the status of the task to reflect that it is now in progress again and clears the completion timestamp since the task is no longer considered completed.
     * @return true if the task was successfully reopened, false otherwise. In this implementation, it always returns true after successfully updating the status and clearing the timestamp, but in a more complex implementation, it could include additional logic to determine if the reopening was successful.
     */
    public boolean reopen() {
        this.status = this.status.transitionTo(WorkOrderTaskStatus.DOING);
        this.completedAt = null;
        return true;
    }

    /**
     * Updates the details of the task, including the service, assigned mechanic, description, and price. If the task is completed, it throws an IllegalStateException to prevent modifications. It updates the serviceId, assignedMechanicId, and description with the new values provided. It then recalculates the total price of the task by summing the new price with the total amounts of all active (non-deleted) products associated with the task. This ensures that any changes to the price are reflected in the overall cost of the task while maintaining the integrity of the product costs.
     * @param serviceId the new service identifier to be associated with the task. This allows for changing the service that the task is related to, which may affect the type of work being performed and potentially the price.
     * @param mechanicId the new mechanic identifier to be assigned to the task. This allows for changing the mechanic responsible for performing the task, which may be necessary if there are scheduling changes or if the original mechanic is unavailable.
     * @param description the new description of the task, providing updated details about the work to be performed. This allows for clarifying or modifying the instructions for the task as needed.
     * @param newLaborPrice the new labor price associated with the potentially updated service.
     */
    public void updateDetails(ServiceId serviceId, MechanicId mechanicId, TaskDescription description, Money newLaborPrice) {
        if (this.status == WorkOrderTaskStatus.COMPLETED) {
            throw new IllegalStateException("operations.error.task.cannotModifyCompletedTask");
        }
        this.serviceId = serviceId;
        this.assignedMechanicId = mechanicId;
        this.description = description;

        this.price = this.products.stream()
                .filter(p -> !p.isDeleted())
                .map(WorkOrderTaskProduct::getTotalAmount)
                .reduce(newLaborPrice, Money::plus);
    }

    /**
     * Updates the quantity of a specific product associated with the task. If the task is completed, it throws an IllegalStateException to prevent modifications. It searches for the product in the list of products associated with the task, and if found, it updates the quantity and recalculates the total price of the task by adjusting for the change in the product's total amount. If the product is not found, it throws an IllegalArgumentException indicating that the product was not found in the task. The method returns the old quantity of the product before the update, allowing callers to know how much the quantity was changed.
     * @param productId the identifier of the product whose quantity is to be updated. The method will look for this product in the list of products associated with the task, and if found, it will update its quantity and adjust the total price of the task accordingly. If the product is not found, an exception will be thrown.
     * @param newQuantity the new quantity to be set for the specified product. This will replace the existing quantity of the product in the task, and the total price of the task will be recalculated based on this new quantity and the unit price of the product. The method returns the old quantity of the product before the update, allowing callers to know how much the quantity was changed.
     * @return the old quantity of the product before the update. This allows callers to understand how much the quantity was changed, which can be useful for logging, auditing, or further processing after the update is made.
     */
    public Quantity updateProductQuantity(ProductId productId, Quantity newQuantity) {
        if (this.status == WorkOrderTaskStatus.COMPLETED) {
            throw new IllegalStateException("operations.error.task.cannotModifyCompletedTask");
        }

        WorkOrderTaskProduct product = this.products.stream()
                .filter(p -> p.getProductId().equals(productId) && !p.isDeleted())
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("operations.error.taskProduct.notFound"));

        Quantity oldQuantity = product.getQuantity();
        Money oldTotalAmount = product.getTotalAmount();

        product.updateQuantity(newQuantity);

        this.price = this.price.minus(oldTotalAmount).plus(product.getTotalAmount());

        return oldQuantity;
    }

    /**
     * Checks if the task has been marked as deleted by verifying if the deletedAt timestamp is not null. This method is used to determine if the task has been logically deleted from the system, which is important for ensuring that deleted tasks are not included in active queries or operations. If the deletedAt field has a value, it indicates that the task has been marked as deleted; otherwise, it is considered active.
     * @return true if the task is marked as deleted (i.e., deletedAt is not null), false otherwise. This allows callers to easily check the deletion status of the task and handle it accordingly in their logic, such as excluding deleted tasks from results or preventing operations on them.
     */
    public boolean isDeleted() {
        return this.deletedAt != null;
    }
}
