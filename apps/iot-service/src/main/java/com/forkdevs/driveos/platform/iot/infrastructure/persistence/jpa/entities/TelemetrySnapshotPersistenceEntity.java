package com.forkdevs.driveos.platform.iot.infrastructure.persistence.jpa.entities;

import com.forkdevs.driveos.platform.iot.domain.model.valueobjects.Obd2DeviceRegistrationId;
import com.forkdevs.driveos.platform.shared.domain.model.valueobjects.BranchId;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.domain.Persistable;

import java.time.Instant;
import java.util.UUID;

/**
 * JPA entity representing the "telemetry_snapshots" table.
 */
@Entity
@Table(name = "telemetry_snapshots")
@Getter
@Setter
@NoArgsConstructor
public class TelemetrySnapshotPersistenceEntity implements Persistable<UUID> {

    @Id
    @Column(columnDefinition = "uuid", nullable = false)
    private UUID id;

    @Override
    public boolean isNew() {
        return createdAt == null;
    }

    @Embedded
    @AttributeOverride(name = "value", column = @Column(name = "obd2_device_registration_id", nullable = false))
    private Obd2DeviceRegistrationId obd2DeviceRegistrationId;

    @Embedded
    @AttributeOverride(name = "value", column = @Column(name = "branch_id", nullable = false))
    private BranchId branchId;

    @Column(nullable = false)
    private Integer rpm;

    @Column(nullable = false)
    private Integer temperature;

    @Column(name = "speed_kmh")
    private Double speedKmh;

    @Column(name = "odometer_km")
    private Integer odometerKm;

    @Column(name = "fuel_level_percent", nullable = false)
    private Double fuelLevelPercent;

    @Column(name = "created_at", nullable = false, updatable = false)
    private Instant createdAt;
}
