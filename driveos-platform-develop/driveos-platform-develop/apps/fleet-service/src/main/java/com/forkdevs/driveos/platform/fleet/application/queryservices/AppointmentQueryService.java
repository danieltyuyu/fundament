package com.forkdevs.driveos.platform.fleet.application.queryservices;

import com.forkdevs.driveos.platform.fleet.domain.model.aggregates.Appointment;
import com.forkdevs.driveos.platform.fleet.domain.model.valueobjects.AppointmentStatus;
import com.forkdevs.driveos.platform.shared.application.result.Result;
import com.forkdevs.driveos.platform.shared.domain.model.valueobjects.BranchId;
import com.forkdevs.driveos.platform.shared.domain.model.valueobjects.CustomerId;
import com.forkdevs.driveos.platform.shared.domain.model.valueobjects.VehicleId;

import java.util.List;
import java.util.UUID;

public interface AppointmentQueryService {

    Result<List<Appointment>, AppointmentQueryFailure> handle(BranchId branchId);
    Result<List<Appointment>, AppointmentQueryFailure> handle(
            BranchId branchId, AppointmentStatus status);
    Result<Appointment, AppointmentQueryFailure> handle(UUID appointmentId);
    Result<List<Appointment>, AppointmentQueryFailure> handle(CustomerId customerId);
    Result<List<Appointment>, AppointmentQueryFailure> handle(VehicleId vehicleId);
}
