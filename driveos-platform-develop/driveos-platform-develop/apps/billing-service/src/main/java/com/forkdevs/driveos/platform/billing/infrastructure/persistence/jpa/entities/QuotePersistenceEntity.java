package com.forkdevs.driveos.platform.billing.infrastructure.persistence.jpa.entities;

import com.forkdevs.driveos.platform.billing.domain.model.valueobjects.QuoteStatus;
import com.forkdevs.driveos.platform.shared.infrastructure.persistence.jpa.entities.AuditableAbstractPersistenceEntity;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.UUID;
import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.LastModifiedBy;

/**
 * JPA entity mapping for the Quote aggregate.
 * Reflects the database structure for the 'quotes' table, including automatic
 * persistence metadata (creation/update timestamps and users).
 */
@Entity
@Table(name = "quotes")
@Getter
@Setter
@lombok.NoArgsConstructor
public class QuotePersistenceEntity extends AuditableAbstractPersistenceEntity {

    @Column(nullable = false, columnDefinition = "uuid")
    private UUID workOrderId;

    @Embedded
    @AttributeOverride(name = "value", column = @Column(name = "branch_id", nullable = false))
    private com.forkdevs.driveos.platform.shared.domain.model.valueobjects.BranchId branchId;

    @Column(name = "subtotal_amount", nullable = false)
    @Convert(converter = com.forkdevs.driveos.platform.shared.infrastructure.persistence.jpa.converters.MoneyAttributeConverter.class)
    private com.forkdevs.driveos.platform.shared.domain.model.valueobjects.Money subtotalAmount;

    @Column(nullable = false)
    private Double discountPercentage;

    @Column(name = "total_amount", nullable = false)
    @Convert(converter = com.forkdevs.driveos.platform.shared.infrastructure.persistence.jpa.converters.MoneyAttributeConverter.class)
    private com.forkdevs.driveos.platform.shared.domain.model.valueobjects.Money totalAmount;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private QuoteStatus status;

    @CreatedBy
    @Column(name = "created_by")
    private UUID createdBy;

    @LastModifiedBy
    @Column(name = "updated_by")
    private UUID updatedBy;
}
