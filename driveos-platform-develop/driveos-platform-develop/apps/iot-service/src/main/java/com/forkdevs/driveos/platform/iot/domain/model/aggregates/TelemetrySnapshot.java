package com.forkdevs.driveos.platform.iot.domain.model.aggregates;

import com.forkdevs.driveos.platform.iot.domain.model.valueobjects.Obd2DeviceRegistrationId;
import com.forkdevs.driveos.platform.iot.domain.model.valueobjects.TelemetrySnapshotId;
import com.forkdevs.driveos.platform.shared.domain.model.aggregates.AbstractDomainAggregateRoot;
import com.forkdevs.driveos.platform.shared.domain.model.valueobjects.BranchId;
import lombok.Getter;

import java.time.Instant;

/**
 * Domain Aggregate Root representing a telemetry snapshot captured by an OBD2 device.
 */
@Getter
public class TelemetrySnapshot extends AbstractDomainAggregateRoot<TelemetrySnapshot> {

    private TelemetrySnapshotId id;
    private Obd2DeviceRegistrationId obd2DeviceRegistrationId;
    private BranchId branchId;
    private Integer rpm;
    private Integer temperature;
    private Double speedKmh;
    private Integer odometerKm;
    private Double fuelLevelPercent;
    private Instant createdAt;

    public TelemetrySnapshot() {
    }

    public TelemetrySnapshot(Obd2DeviceRegistrationId obd2DeviceRegistrationId, BranchId branchId, Integer rpm, Integer temperature, Double speedKmh, Integer odometerKm, Double fuelLevelPercent, Instant createdAt) {
        this.id = TelemetrySnapshotId.random();
        this.obd2DeviceRegistrationId = obd2DeviceRegistrationId;
        this.branchId = branchId;
        this.rpm = rpm;
        this.temperature = temperature;
        this.speedKmh = speedKmh;
        this.odometerKm = odometerKm;
        this.fuelLevelPercent = fuelLevelPercent;
        this.createdAt = createdAt != null ? createdAt : Instant.now();
    }

    public TelemetrySnapshot(TelemetrySnapshotId id, Obd2DeviceRegistrationId obd2DeviceRegistrationId, BranchId branchId, Integer rpm, Integer temperature, Double speedKmh, Integer odometerKm, Double fuelLevelPercent, Instant createdAt) {
        this.id = id;
        this.obd2DeviceRegistrationId = obd2DeviceRegistrationId;
        this.branchId = branchId;
        this.rpm = rpm;
        this.temperature = temperature;
        this.speedKmh = speedKmh;
        this.odometerKm = odometerKm;
        this.fuelLevelPercent = fuelLevelPercent;
        this.createdAt = createdAt;
    }
}
