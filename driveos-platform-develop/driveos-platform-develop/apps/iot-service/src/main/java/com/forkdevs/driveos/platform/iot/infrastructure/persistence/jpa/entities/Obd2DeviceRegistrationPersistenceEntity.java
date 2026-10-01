package com.forkdevs.driveos.platform.iot.infrastructure.persistence.jpa.entities;

import com.forkdevs.driveos.platform.iot.domain.model.valueobjects.Obd2DeviceId;
import com.forkdevs.driveos.platform.shared.domain.model.valueobjects.BranchId;
import com.forkdevs.driveos.platform.shared.domain.model.valueobjects.VehicleId;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.SQLRestriction;
import org.springframework.data.domain.Persistable;

import java.time.Instant;
import java.util.UUID;

/**
 * JPA entity representing the "obd2_device_registrations" table.
 */
@Entity
@Table(name = "obd2_device_registrations")
@SQLDelete(sql = "UPDATE obd2_device_registrations SET deleted_at = CURRENT_TIMESTAMP, status = 'INACTIVE' WHERE id = ?")
@SQLRestriction("deleted_at IS NULL")
@Getter
@Setter
@NoArgsConstructor
public class Obd2DeviceRegistrationPersistenceEntity implements Persistable<UUID> {

    @Id
    @Column(columnDefinition = "uuid", nullable = false)
    private UUID id;

    @Override
    public boolean isNew() {
        return createdAt == null;
    }

    @Embedded
    @AttributeOverride(name = "value", column = @Column(name = "obd2_device_id", nullable = false))
    private Obd2DeviceId obd2DeviceId;

    @Embedded
    @AttributeOverride(name = "value", column = @Column(name = "branch_id", nullable = false))
    private BranchId branchId;

    @Embedded
    @AttributeOverride(name = "value", column = @Column(name = "vehicle_id", nullable = false))
    private VehicleId vehicleId;

    @Column(nullable = false)
    private String status;

    @Column(name = "created_at", nullable = false, updatable = false)
    private Instant createdAt;

    @Column(name = "deleted_at")
    private Instant deletedAt;
}
