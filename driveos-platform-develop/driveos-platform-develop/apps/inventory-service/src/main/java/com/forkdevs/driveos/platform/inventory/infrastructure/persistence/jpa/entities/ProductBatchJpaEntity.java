package com.forkdevs.driveos.platform.inventory.infrastructure.persistence.jpa.entities;

import com.forkdevs.driveos.platform.shared.domain.model.valueobjects.Money;
import com.forkdevs.driveos.platform.shared.infrastructure.persistence.jpa.converters.MoneyAttributeConverter;
import com.forkdevs.driveos.platform.shared.infrastructure.persistence.jpa.entities.AuditableAbstractPersistenceEntity;
import jakarta.persistence.*;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.SQLRestriction;
import org.springframework.data.domain.Persistable;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "product_batches")
@EntityListeners(AuditingEntityListener.class)
@SQLDelete(sql = "UPDATE product_batches SET deleted_at = CURRENT_TIMESTAMP WHERE id = ? AND version = ?")
@SQLRestriction("deleted_at IS NULL")
public class ProductBatchJpaEntity extends AuditableAbstractPersistenceEntity implements Persistable<UUID> {

    @Override
    public boolean isNew() {
        return getCreatedAt() == null;
    }

    @Column(name = "branch_id", nullable = false)
    private UUID branchId;

    @Column(name = "initial_quantity", nullable = false)
    private Integer initialQuantity;

    @Column(name = "available_quantity", nullable = false)
    private Integer availableQuantity;

    @Column(name = "acquisition_cost", nullable = false)
    @Convert(converter = MoneyAttributeConverter.class)
    private Money acquisitionCost;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "product_id", nullable = false)
    private ProductJpaEntity product;

    @Column(name = "deleted_at")
    private Instant deletedAt;

    @Version
    private Long version;

    public ProductBatchJpaEntity() {}

    public UUID getBranchId() { return branchId; }
    public void setBranchId(UUID branchId) { this.branchId = branchId; }
    public Integer getInitialQuantity() { return initialQuantity; }
    public void setInitialQuantity(Integer initialQuantity) { this.initialQuantity = initialQuantity; }
    public Integer getAvailableQuantity() { return availableQuantity; }
    public void setAvailableQuantity(Integer availableQuantity) { this.availableQuantity = availableQuantity; }
    public Money getAcquisitionCost() { return acquisitionCost; }
    public void setAcquisitionCost(Money acquisitionCost) { this.acquisitionCost = acquisitionCost; }
    public ProductJpaEntity getProduct() { return product; }
    public void setProduct(ProductJpaEntity product) { this.product = product; }
    public Instant getDeletedAt() { return deletedAt; }
    public void setDeletedAt(Instant deletedAt) { this.deletedAt = deletedAt; }
    public Long getVersion() { return version; }
    public void setVersion(Long version) { this.version = version; }
}
