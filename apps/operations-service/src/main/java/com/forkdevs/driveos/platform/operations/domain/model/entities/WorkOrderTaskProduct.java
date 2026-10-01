package com.forkdevs.driveos.platform.operations.domain.model.entities;

import com.forkdevs.driveos.platform.operations.domain.model.valueobjects.ProductId;
import com.forkdevs.driveos.platform.operations.domain.model.valueobjects.Quantity;
import com.forkdevs.driveos.platform.operations.domain.model.valueobjects.WorkOrderTaskProductId;
import com.forkdevs.driveos.platform.shared.domain.model.valueobjects.BranchId;
import com.forkdevs.driveos.platform.shared.domain.model.valueobjects.Money;

import lombok.Getter;

import java.time.Instant;
import java.util.UUID;

/**
 * Entity representing a product associated with a work order task. This entity is part of the Work Order Task aggregate and contains details about the product, such as its ID, quantity, unit price, and total amount. It also includes auditing fields for tracking creation and modification times, as well as soft deletion handling.
 * The @SQLDelete annotation is used to implement soft deletion by updating the deleted_at timestamp instead of physically removing the record from the database. The @SQLRestriction annotation ensures that only records that have not been marked as deleted (i.e., where deleted_at is null) are returned in queries.
 * @author Joel Huamani Estefanero
 */
@Getter

public class WorkOrderTaskProduct {

    private WorkOrderTaskProductId id;
    private ProductId productId;
    private BranchId branchId;
    private Quantity quantity;
    private Money unitPrice;
    private Money totalAmount;
    private Instant createdAt;
    private Instant updatedAt;
    private Instant deletedAt;
    private Long version;

    public WorkOrderTaskProduct() {}

    public WorkOrderTaskProduct(WorkOrderTaskProductId id, ProductId productId, BranchId branchId, Quantity quantity, Money unitPrice, Money totalAmount, Instant createdAt, Instant updatedAt, Instant deletedAt, Long version) {
        this.id = id;
        this.productId = productId;
        this.branchId = branchId;
        this.quantity = quantity;
        this.unitPrice = unitPrice;
        this.totalAmount = totalAmount;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
        this.deletedAt = deletedAt;
        this.version = version;
    }

    /**
     * Constructor to create a new WorkOrderTaskProduct instance with the specified product ID, branch ID, quantity, and unit price. The total amount is calculated by multiplying the unit price by the quantity. A unique UUID is generated for the entity ID.
     * @param productId the unique identifier of the product associated with the work order task
     * @param branchId the unique identifier of the branch where the product is located
     * @param quantity the quantity of the product being used in the work order task
     * @param unitPrice the unit price of the product
     */
    public WorkOrderTaskProduct(ProductId productId, BranchId branchId, Quantity quantity, Money unitPrice) {
        this.id = new WorkOrderTaskProductId(UUID.randomUUID());
        this.productId = productId;
        this.branchId = branchId;
        this.quantity = quantity;
        this.unitPrice = unitPrice;
        this.totalAmount = unitPrice.multiply(quantity.value());
    }

    /**
     * Updates the quantity of the product and recalculates the total amount based on the new quantity and the existing unit price. This method allows for modifying the quantity of the product in the work order task while ensuring that the total amount remains accurate.
     * @param newQuantity the new quantity to be set for the product. This value will be used to update the quantity field and recalculate the total amount accordingly.
     */
    public void updateQuantity(Quantity newQuantity) {
        this.quantity = newQuantity;
        this.totalAmount = this.unitPrice.multiply(newQuantity.value());
    }

    /**
     * Checks if this WorkOrderTaskProduct has been marked as deleted by verifying if the deletedAt timestamp is not null. This method is used to determine if the entity has been soft-deleted, which means it has been marked as deleted without being physically removed from the database.
     * @return true if the deletedAt timestamp is not null (indicating that the entity has been marked as deleted), false otherwise (indicating that the entity is still active and has not been marked as deleted).
     */
    public boolean isDeleted() {
        return this.deletedAt != null;
    }
}
