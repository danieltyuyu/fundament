package com.forkdevs.driveos.platform.iot.domain.model.aggregates;

import com.forkdevs.driveos.platform.iot.domain.model.valueobjects.Obd2DeviceId;
import com.forkdevs.driveos.platform.iot.domain.model.valueobjects.Obd2DeviceRegistrationId;
import com.forkdevs.driveos.platform.iot.domain.model.valueobjects.Obd2RegistrationStatus;
import com.forkdevs.driveos.platform.shared.domain.model.aggregates.AbstractDomainAggregateRoot;
import com.forkdevs.driveos.platform.shared.domain.model.valueobjects.BranchId;
import com.forkdevs.driveos.platform.shared.domain.model.valueobjects.VehicleId;
import lombok.Getter;

import java.time.Instant;

/**
 * Domain Aggregate Root representing the registration/linking of an OBD2 device to a vehicle in a branch.
 */
@Getter
public class Obd2DeviceRegistration extends AbstractDomainAggregateRoot<Obd2DeviceRegistration> {

    private Obd2DeviceRegistrationId id;
    private Obd2DeviceId obd2DeviceId;
    private BranchId branchId;
    private VehicleId vehicleId;
    private Obd2RegistrationStatus status;
    private Instant createdAt;
    private Instant deletedAt;

    public Obd2DeviceRegistration() {
    }

    public Obd2DeviceRegistration(Obd2DeviceId obd2DeviceId, BranchId branchId, VehicleId vehicleId) {
        this.id = Obd2DeviceRegistrationId.random();
        this.obd2DeviceId = obd2DeviceId;
        this.branchId = branchId;
        this.vehicleId = vehicleId;
        this.status = Obd2RegistrationStatus.ACTIVE;
        this.createdAt = Instant.now();
    }

    public Obd2DeviceRegistration(Obd2DeviceRegistrationId id, Obd2DeviceId obd2DeviceId, BranchId branchId, VehicleId vehicleId, Obd2RegistrationStatus status, Instant createdAt, Instant deletedAt) {
        this.id = id;
        this.obd2DeviceId = obd2DeviceId;
        this.branchId = branchId;
        this.vehicleId = vehicleId;
        this.status = status;
        this.createdAt = createdAt;
        this.deletedAt = deletedAt;
    }

    /**
     * Deactivates/Unlinks the device registration.
     */
    public void deactivate() {
        if (Obd2RegistrationStatus.INACTIVE.equals(this.status)) {
            throw new IllegalStateException("iot.error.obd2DeviceRegistration.alreadyInactive");
        }
        this.status = Obd2RegistrationStatus.INACTIVE;
        this.deletedAt = Instant.now();
    }
}
