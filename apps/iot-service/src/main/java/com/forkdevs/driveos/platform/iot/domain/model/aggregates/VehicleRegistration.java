package com.forkdevs.driveos.platform.iot.domain.model.aggregates;

import com.forkdevs.driveos.platform.iot.domain.model.valueobjects.VehicleRegistrationId;
import com.forkdevs.driveos.platform.iot.domain.model.valueobjects.VehicleRegistrationStatus;
import com.forkdevs.driveos.platform.shared.domain.model.aggregates.AbstractDomainAggregateRoot;
import com.forkdevs.driveos.platform.shared.domain.model.valueobjects.VehicleId;
import lombok.Getter;

import java.time.Instant;
import java.util.UUID;

/**
 * Domain Aggregate Root representing a Vehicle Registration coupling a vehicle to a driver user.
 */
@Getter
public class VehicleRegistration extends AbstractDomainAggregateRoot<VehicleRegistration> {

    private VehicleRegistrationId id;
    private UUID userId;
    private VehicleId vehicleId;
    private VehicleRegistrationStatus status;
    private Instant createdAt;
    private Instant deletedAt;

    public VehicleRegistration() {
    }

    public VehicleRegistration(UUID userId, VehicleId vehicleId) {
        this.id = VehicleRegistrationId.random();
        this.userId = userId;
        this.vehicleId = vehicleId;
        this.status = VehicleRegistrationStatus.ACTIVE;
        this.createdAt = Instant.now();
    }

    public VehicleRegistration(
            VehicleRegistrationId id,
            UUID userId,
            VehicleId vehicleId,
            VehicleRegistrationStatus status,
            Instant createdAt,
            Instant deletedAt
    ) {
        this.id = id;
        this.userId = userId;
        this.vehicleId = vehicleId;
        this.status = status;
        this.createdAt = createdAt;
        this.deletedAt = deletedAt;
    }

    /**
     * Deactivates the registration (setting status to PREVIOUS and marking logic delete/unlinking).
     */
    public void deactivateRegistration() {
        this.status = VehicleRegistrationStatus.PREVIOUS;
        this.deletedAt = Instant.now();
    }
}
