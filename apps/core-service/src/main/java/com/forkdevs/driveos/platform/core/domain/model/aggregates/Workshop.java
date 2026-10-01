package com.forkdevs.driveos.platform.core.domain.model.aggregates;

import com.forkdevs.driveos.platform.core.domain.model.valueobjects.OwnerId;
import com.forkdevs.driveos.platform.core.domain.model.valueobjects.TaxId;
import com.forkdevs.driveos.platform.core.domain.model.valueobjects.WorkshopId;
import com.forkdevs.driveos.platform.shared.domain.model.aggregates.AbstractDomainAggregateRoot;

import lombok.Getter;

import java.time.Instant;
import java.util.UUID;

@Getter
public class Workshop extends AbstractDomainAggregateRoot<Workshop> {

    private WorkshopId id;
    private OwnerId ownerId;
    private String businessName;
    private String brandName;
    private TaxId taxId;
    private int mileageIntervalConfig;
    private Instant createdAt;
    private Instant updatedAt;
    private Instant deletedAt;
    private Long version;

    public Workshop() {
        this.mileageIntervalConfig = 1;
    }

    public Workshop(WorkshopId id, OwnerId ownerId, String businessName, String brandName, TaxId taxId, int mileageIntervalConfig, Instant createdAt, Instant updatedAt, Instant deletedAt, Long version) {
        if (businessName == null || businessName.isBlank()) throw new IllegalArgumentException("core.error.businessName.required");
        if (brandName == null || brandName.isBlank()) throw new IllegalArgumentException("core.error.brandName.required");
        this.id = id;
        this.ownerId = ownerId;
        this.businessName = businessName;
        this.brandName = brandName;
        this.taxId = taxId;
        this.mileageIntervalConfig = mileageIntervalConfig;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
        this.deletedAt = deletedAt;
        this.version = version;
    }

    public Workshop(OwnerId ownerId, String businessName, String brandName, TaxId taxId, int mileageIntervalConfig) {
        if (businessName == null || businessName.isBlank()) throw new IllegalArgumentException("core.error.businessName.required");
        if (brandName == null || brandName.isBlank()) throw new IllegalArgumentException("core.error.brandName.required");
        this.id = new WorkshopId(UUID.randomUUID());
        this.ownerId = ownerId;
        this.businessName = businessName;
        this.brandName = brandName;
        this.taxId = taxId;
        this.mileageIntervalConfig = mileageIntervalConfig;
    }

    public void update(String businessName, String brandName, TaxId taxId, int mileageIntervalConfig) {
        if (businessName == null || businessName.isBlank()) throw new IllegalArgumentException("core.error.businessName.required");
        if (brandName == null || brandName.isBlank()) throw new IllegalArgumentException("core.error.brandName.required");

        this.businessName = businessName;
        this.brandName = brandName;
        this.taxId = taxId;
        this.mileageIntervalConfig = mileageIntervalConfig;
    }
}
