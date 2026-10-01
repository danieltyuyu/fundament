package com.forkdevs.driveos.platform.inventory.domain.model.entities;

import com.forkdevs.driveos.platform.inventory.domain.model.valueobjects.InventoryQuantity;
import com.forkdevs.driveos.platform.shared.domain.model.valueobjects.Money;

import java.util.Date;
import java.util.UUID;

public class ProductBatch {
    private final UUID batchId;
    private final InventoryQuantity initialQuantity;
    private InventoryQuantity availableQuantity;
    private final Money acquisitionCost;
    private final Date receptionDate;
    private Long version;

    public ProductBatch(UUID batchId, InventoryQuantity initialQuantity, Money acquisitionCost) {
        this.batchId = batchId != null ? batchId : UUID.randomUUID();
        this.initialQuantity = initialQuantity;
        this.availableQuantity = initialQuantity;
        this.acquisitionCost = acquisitionCost;
        this.receptionDate = new Date();
    }

    private ProductBatch(UUID batchId, InventoryQuantity initialQuantity, InventoryQuantity availableQuantity, Money acquisitionCost, Date receptionDate, Long version) {
        this.batchId = batchId;
        this.initialQuantity = initialQuantity;
        this.availableQuantity = availableQuantity;
        this.acquisitionCost = acquisitionCost;
        this.receptionDate = receptionDate != null ? receptionDate : new Date();
        this.version = version;
    }

    public static ProductBatch reconstitute(UUID batchId, InventoryQuantity initialQuantity, InventoryQuantity availableQuantity, Money acquisitionCost, Date receptionDate, Long version) {
        return new ProductBatch(batchId, initialQuantity, availableQuantity, acquisitionCost, receptionDate, version);
    }

    public UUID getBatchId() { return batchId; }
    public InventoryQuantity getInitialQuantity() { return initialQuantity; }
    public InventoryQuantity getAvailableQuantity() { return availableQuantity; }
    public Money getAcquisitionCost() { return acquisitionCost; }
    public Date getReceptionDate() { return receptionDate; }
    public Long getVersion() { return version; }
    
    public void deductQuantity(InventoryQuantity amount) { this.availableQuantity = this.availableQuantity.subtract(amount); }
    public void addQuantity(InventoryQuantity amount) { this.availableQuantity = this.availableQuantity.add(amount); }
}
