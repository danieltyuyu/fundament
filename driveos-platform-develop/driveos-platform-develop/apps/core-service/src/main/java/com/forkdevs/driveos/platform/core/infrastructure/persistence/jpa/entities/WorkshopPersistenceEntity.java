package com.forkdevs.driveos.platform.core.infrastructure.persistence.jpa.entities;

import com.forkdevs.driveos.platform.shared.infrastructure.persistence.jpa.entities.AuditableAbstractPersistenceEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import jakarta.persistence.Version;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.SQLRestriction;
import org.springframework.data.domain.Persistable;

import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "workshops")
@SQLDelete(sql = "UPDATE workshops SET deleted_at = CURRENT_TIMESTAMP WHERE id = ? AND version = ?")
@SQLRestriction("deleted_at IS NULL")
@Getter
@Setter
@NoArgsConstructor
public class WorkshopPersistenceEntity extends AuditableAbstractPersistenceEntity implements Persistable<UUID> {

    @Override
    public boolean isNew() {
        return getCreatedAt() == null;
    }

    @Column(name = "owner_id", nullable = false)
    private UUID ownerId;

    @Column(name = "business_name", nullable = false)
    private String businessName;

    @Column(name = "brand_name", nullable = false)
    private String brandName;

    @Column(name = "tax_id", nullable = false)
    private String taxId;

    @Column(name = "mileage_interval_config", nullable = false)
    private int mileageIntervalConfig;

    @Column(name = "deleted_at")
    private Instant deletedAt;

    @Version
    private Long version;
}
