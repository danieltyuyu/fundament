package com.forkdevs.driveos.platform.fleet.domain.model.aggregates;

import com.forkdevs.driveos.platform.fleet.domain.model.valueobjects.AppointmentStatus;
import com.forkdevs.driveos.platform.fleet.domain.model.valueobjects.AppointmentSummary;
import com.forkdevs.driveos.platform.shared.domain.model.aggregates.AbstractDomainAggregateRoot;
import com.forkdevs.driveos.platform.shared.domain.model.valueobjects.BranchId;
import com.forkdevs.driveos.platform.shared.domain.model.valueobjects.CustomerId;
import com.forkdevs.driveos.platform.shared.domain.model.valueobjects.VehicleId;
import lombok.Getter;

import java.time.Instant;
import java.time.LocalDateTime;
import java.util.UUID;

@Getter
public class Appointment extends AbstractDomainAggregateRoot<Appointment> {

    private UUID id;
    private BranchId branchId;
    private CustomerId customerId;
    private VehicleId vehicleId;
    private LocalDateTime scheduledStart;
    private LocalDateTime scheduledEnd;
    private AppointmentStatus status;
    private AppointmentSummary notes;
    private Instant createdAt;
    private Instant updatedAt;
    private Instant deletedAt;
    private UUID createdBy;
    private UUID updatedBy;
    private Long version;

    public Appointment() {
    }

    public Appointment(
            UUID id,
            BranchId branchId,
            CustomerId customerId,
            VehicleId vehicleId,
            LocalDateTime scheduledStart,
            LocalDateTime scheduledEnd,
            AppointmentStatus status,
            AppointmentSummary notes,
            Instant createdAt,
            Instant updatedAt,
            Instant deletedAt,
            UUID createdBy,
            UUID updatedBy,
            Long version
    ) {
        this.id = id;
        this.branchId = branchId;
        this.customerId = customerId;
        this.vehicleId = vehicleId;
        this.scheduledStart = scheduledStart;
        this.scheduledEnd = scheduledEnd;
        this.status = status;
        this.notes = notes;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
        this.deletedAt = deletedAt;
        this.createdBy = createdBy;
        this.updatedBy = updatedBy;
        this.version = version;
    }

    public Appointment(
            BranchId branchId,
            CustomerId customerId,
            VehicleId vehicleId,
            LocalDateTime scheduledStart,
            AppointmentSummary notes
    ) {
        if (branchId == null) {
            throw new IllegalArgumentException("Branch ID is required");
        }
        if (customerId == null) {
            throw new IllegalArgumentException("Customer ID is required");
        }
        if (vehicleId == null) {
            throw new IllegalArgumentException("Vehicle ID is required");
        }
        if (scheduledStart == null) {
            throw new IllegalArgumentException("Scheduled start is required");
        }

        this.branchId = branchId;
        this.customerId = customerId;
        this.vehicleId = vehicleId;
        this.scheduledStart = scheduledStart;
        this.scheduledEnd = scheduledStart.plusHours(1);
        this.status = AppointmentStatus.PENDING;
        this.notes = notes;
    }

    public void update(
            BranchId branchId,
            CustomerId customerId,
            VehicleId vehicleId,
            LocalDateTime scheduledStart,
            AppointmentStatus status,
            AppointmentSummary notes
    ) {
        if (branchId == null) {
            throw new IllegalArgumentException("Branch ID is required");
        }
        if (customerId == null) {
            throw new IllegalArgumentException("Customer ID is required");
        }
        if (vehicleId == null) {
            throw new IllegalArgumentException("Vehicle ID is required");
        }
        if (scheduledStart == null) {
            throw new IllegalArgumentException("Scheduled start is required");
        }
        if (status == null) {
            throw new IllegalArgumentException("Status is required");
        }

        this.branchId = branchId;
        this.customerId = customerId;
        this.vehicleId = vehicleId;
        this.scheduledStart = scheduledStart;
        this.scheduledEnd = scheduledStart.plusHours(1);
        this.status = status;
        this.notes = notes;
    }
}