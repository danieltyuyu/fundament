package com.forkdevs.driveos.platform.fleet.domain.model.commands;

import com.forkdevs.driveos.platform.fleet.domain.model.valueobjects.AppointmentSummary;
import com.forkdevs.driveos.platform.shared.domain.model.valueobjects.BranchId;
import com.forkdevs.driveos.platform.shared.domain.model.valueobjects.CustomerId;
import com.forkdevs.driveos.platform.shared.domain.model.valueobjects.VehicleId;

import java.time.LocalDateTime;

public record CreateAppointmentCommand(
        BranchId branchId,
        CustomerId customerId,
        VehicleId vehicleId,
        LocalDateTime scheduledStart,
        AppointmentSummary notes
) {
    public CreateAppointmentCommand {
        if (branchId == null) throw new IllegalArgumentException("Branch ID is required");
        if (customerId == null) throw new IllegalArgumentException("Customer ID is required");
        if (vehicleId == null) throw new IllegalArgumentException("Vehicle ID is required");
        if (scheduledStart == null) throw new IllegalArgumentException("Scheduled start is required");
    }
}