package com.forkdevs.driveos.platform.inventory.domain.model.aggregates;

import com.forkdevs.driveos.platform.inventory.domain.exceptions.InsufficientStockException;
import com.forkdevs.driveos.platform.inventory.domain.model.entities.ProductBatch;
import com.forkdevs.driveos.platform.inventory.domain.model.events.ProductCreatedEvent;
import com.forkdevs.driveos.platform.inventory.domain.model.valueobjects.*;
import com.forkdevs.driveos.platform.shared.domain.model.valueobjects.BranchId;
import com.forkdevs.driveos.platform.shared.domain.model.valueobjects.Money;
import org.springframework.data.domain.AbstractAggregateRoot;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

public class Product extends AbstractAggregateRoot<Product> {
    private final UUID id;
    private final BranchId branchId;
    private ProductCategory category;
    private ProductName name;
    private Sku sku;
    private InventoryQuantity currentStock;
    private Money currentSellingPrice;
    private String description;
    private Integer minimumStock;
    private Long version;
    private final List<ProductBatch> batches;

    public Product(UUID id, BranchId branchId, ProductCategory category, ProductName name, Sku sku, Money currentSellingPrice, String description, Integer minimumStock) {
        this.id = id != null ? id : UUID.randomUUID();
        this.branchId = branchId;
        this.category = category;
        this.name = name;
        this.sku = sku;
        this.currentSellingPrice = currentSellingPrice;
        this.description = description;
        this.minimumStock = minimumStock;
        this.currentStock = new InventoryQuantity(0);
        this.batches = new ArrayList<>();
        this.registerEvent(new ProductCreatedEvent(this, this.branchId, this.id));
    }

    private Product(UUID id, BranchId branchId, ProductCategory category, ProductName name, Sku sku, InventoryQuantity currentStock, Money currentSellingPrice, String description, Integer minimumStock, Long version) {
        this.id = id;
        this.branchId = branchId;
        this.category = category;
        this.name = name;
        this.sku = sku;
        this.currentStock = currentStock;
        this.currentSellingPrice = currentSellingPrice;
        this.description = description;
        this.minimumStock = minimumStock;
        this.version = version;
        this.batches = new ArrayList<>();
    }

    public static Product reconstitute(UUID id, BranchId branchId, ProductCategory category, ProductName name, Sku sku, InventoryQuantity currentStock, Money currentSellingPrice, String description, Integer minimumStock, Long version, List<ProductBatch> batches) {
        Product p = new Product(id, branchId, category, name, sku, currentStock, currentSellingPrice, description, minimumStock, version);
        if (batches != null) {
            p.batches.addAll(batches);
        }
        return p;
    }

    public UUID getId() { return id; }
    public BranchId getBranchId() { return branchId; }
    public ProductCategory getCategory() { return category; }
    public ProductName getName() { return name; }
    public Sku getSku() { return sku; }
    public InventoryQuantity getCurrentStock() { return currentStock; }
    public Money getCurrentSellingPrice() { return currentSellingPrice; }
    public String getDescription() { return description; }
    public Integer getMinimumStock() { return minimumStock; }
    public Long getVersion() { return version; }
    public List<ProductBatch> getBatches() { return Collections.unmodifiableList(batches); }

    public void addBatch(ProductBatch batch) {
        this.batches.add(batch);
        this.currentStock = this.currentStock.add(batch.getAvailableQuantity());
    }

    public void updateDetails(ProductName name, ProductCategory category, Sku sku, Money currentSellingPrice, String description, Integer minimumStock) {
        this.name = name;
        this.category = category;
        this.sku = sku;
        this.currentSellingPrice = currentSellingPrice;
        this.description = description;
        this.minimumStock = minimumStock;
    }

    public void reserveStock(InventoryQuantity amount) {
        if (this.currentStock.value() < amount.value()) throw new InsufficientStockException("Not enough stock available");
        
        int remainingToDeduct = amount.value();
        for (ProductBatch batch : this.batches) {
            if (remainingToDeduct <= 0) break;
            int batchAvail = batch.getAvailableQuantity().value();
            if (batchAvail > 0) {
                int deduct = Math.min(batchAvail, remainingToDeduct);
                batch.deductQuantity(new InventoryQuantity(deduct));
                remainingToDeduct -= deduct;
            }
        }
        this.currentStock = this.currentStock.subtract(amount);
    }

    public void releaseStock(InventoryQuantity amount) {
        int remainingToAdd = amount.value();
        for (ProductBatch batch : this.batches) {
            if (remainingToAdd <= 0) break;
            int capacity = batch.getInitialQuantity().value() - batch.getAvailableQuantity().value();
            if (capacity > 0) {
                int add = Math.min(capacity, remainingToAdd);
                batch.addQuantity(new InventoryQuantity(add));
                remainingToAdd -= add;
            }
        }
        this.currentStock = this.currentStock.add(amount);
    }
}
