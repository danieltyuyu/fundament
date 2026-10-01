package com.forkdevs.driveos.platform.operations.infrastructure.persistence.jpa.entities;

import org.springframework.data.domain.Persistable;
import com.forkdevs.driveos.platform.operations.domain.model.valueobjects.DiagnosticSummary;
import com.forkdevs.driveos.platform.shared.domain.model.valueobjects.Mileage;
import com.forkdevs.driveos.platform.operations.domain.model.valueobjects.WorkOrderStatus;
import com.forkdevs.driveos.platform.operations.infrastructure.persistence.jpa.converters.DiagnosticSummaryAttributeConverter;
import com.forkdevs.driveos.platform.operations.domain.model.valueobjects.AppointmentId;
import com.forkdevs.driveos.platform.shared.domain.model.valueobjects.BranchId;
import com.forkdevs.driveos.platform.shared.domain.model.valueobjects.CustomerId;
import com.forkdevs.driveos.platform.shared.domain.model.valueobjects.Money;
import com.forkdevs.driveos.platform.shared.domain.model.valueobjects.VehicleId;
import com.forkdevs.driveos.platform.shared.infrastructure.persistence.jpa.converters.MileageAttributeConverter;
import com.forkdevs.driveos.platform.shared.infrastructure.persistence.jpa.converters.MoneyAttributeConverter;
import com.forkdevs.driveos.platform.shared.infrastructure.persistence.jpa.entities.AuditableAbstractPersistenceEntity;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.SQLRestriction;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;
import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.LastModifiedBy;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor

@Entity
@EntityListeners(AuditingEntityListener.class)
@Table(name = "work_orders")
@SQLDelete(sql = "UPDATE work_orders SET deleted_at = CURRENT_TIMESTAMP WHERE id = ? AND version = ?")
@SQLRestriction("deleted_at IS NULL")
public class WorkOrderPersistenceEntity extends AuditableAbstractPersistenceEntity implements Persistable<UUID> {

    @Override
    public boolean isNew() {
        return getCreatedAt() == null;
    }

    @Embedded
    @AttributeOverride(name = "value", column = @Column(name = "appointment_id", nullable = false))
    private AppointmentId appointmentId;

    @Embedded
    @AttributeOverride(name = "value", column = @Column(name = "branch_id", nullable = false))
    private BranchId branchId;

    @Embedded
    @AttributeOverride(name = "value", column = @Column(name = "vehicle_id", nullable = false))
    private VehicleId vehicleId;

    @Embedded
    @AttributeOverride(name = "value", column = @Column(name = "customer_id", nullable = false))
    private CustomerId customerId;

    @Column(name = "internal_number", nullable = false, updatable = false)
    private Integer internalNumber;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private WorkOrderStatus status;

    @Column(name = "diagnostic_summary", nullable = false, columnDefinition = "TEXT")
    @Convert(converter = DiagnosticSummaryAttributeConverter.class)
    private DiagnosticSummary diagnosticSummary;

    @Column(name = "mileage_in", nullable = false)
    @Convert(converter = MileageAttributeConverter.class)
    private Mileage mileageIn;

    @Column(name = "total_amount", nullable = false)
    @Convert(converter = MoneyAttributeConverter.class)
    private Money totalAmount;

    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    @JoinColumn(name = "work_order_id", nullable = false)
    private List<WorkOrderTaskPersistenceEntity> tasks = new ArrayList<>();

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
