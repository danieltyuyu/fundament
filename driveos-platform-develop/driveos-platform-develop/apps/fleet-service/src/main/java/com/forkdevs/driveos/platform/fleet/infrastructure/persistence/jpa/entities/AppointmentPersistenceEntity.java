package com.forkdevs.driveos.platform.fleet.infrastructure.persistence.jpa.entities;

import com.forkdevs.driveos.platform.fleet.domain.model.valueobjects.AppointmentStatus;
import com.forkdevs.driveos.platform.fleet.domain.model.valueobjects.AppointmentSummary;
import com.forkdevs.driveos.platform.fleet.infrastructure.persistence.jpa.converters.AppointmentSummaryAttributeConverter;
import com.forkdevs.driveos.platform.shared.domain.model.valueobjects.BranchId;
import com.forkdevs.driveos.platform.shared.domain.model.valueobjects.CustomerId;
import com.forkdevs.driveos.platform.shared.domain.model.valueobjects.VehicleId;
import com.forkdevs.driveos.platform.shared.infrastructure.persistence.jpa.entities.AuditableAbstractPersistenceEntity;
import jakarta.persistence.AttributeOverride;
import jakarta.persistence.Column;
import jakarta.persistence.Convert;
import jakarta.persistence.Embedded;
import jakarta.persistence.Entity;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Table;
import jakarta.persistence.Version;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.SQLRestriction;
import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.LastModifiedBy;
import org.springframework.data.domain.Persistable;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.Instant;
import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@Entity
@EntityListeners(AuditingEntityListener.class)
@Table(name = "appointments")
@SQLDelete(sql = "UPDATE appointments SET deleted_at = CURRENT_TIMESTAMP WHERE id = ? AND version = ?")
@SQLRestriction("deleted_at IS NULL")
public class AppointmentPersistenceEntity extends AuditableAbstractPersistenceEntity implements Persistable<UUID> {

    @Override
    public boolean isNew() {
        return getCreatedAt() == null;
    }

    @Embedded
    @AttributeOverride(name = "value", column = @Column(name = "branch_id", nullable = false))
    private BranchId branchId;

    @Embedded
    @AttributeOverride(name = "value", column = @Column(name = "customer_id", nullable = false))
    private CustomerId customerId;

    @Embedded
    @AttributeOverride(name = "value", column = @Column(name = "vehicle_id", nullable = false))
    private VehicleId vehicleId;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false, length = 20)
    private AppointmentStatus status;

    @Column(name = "scheduled_start", nullable = false)
    private LocalDateTime scheduledStart;

    @Column(name = "scheduled_end", nullable = false)
    private LocalDateTime scheduledEnd;

    @Column(name = "notes", columnDefinition = "TEXT")
    @Convert(converter = AppointmentSummaryAttributeConverter.class)
    private AppointmentSummary notes;

    @Column(name = "deleted_at")
    private Instant deletedAt;

    @CreatedBy
    @Column(name = "created_by", updatable = false)
    private UUID createdBy;

    @LastModifiedBy
    @Column(name = "updated_by")
    private UUID updatedBy;

    @Version
    private Long version;
}