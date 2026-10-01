package com.forkdevs.driveos.platform.fleet.application.internal.queryservices;

import com.forkdevs.driveos.platform.fleet.application.queryservices.AppointmentQueryFailure;
import com.forkdevs.driveos.platform.fleet.application.queryservices.AppointmentQueryService;
import com.forkdevs.driveos.platform.fleet.domain.model.aggregates.Appointment;
import com.forkdevs.driveos.platform.fleet.domain.model.valueobjects.AppointmentStatus;
import com.forkdevs.driveos.platform.fleet.domain.repositories.AppointmentRepository;
import com.forkdevs.driveos.platform.shared.application.result.Result;
import com.forkdevs.driveos.platform.shared.domain.model.valueobjects.BranchId;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
public class AppointmentQueryServiceImpl implements AppointmentQueryService {

    private final AppointmentRepository appointmentRepository;

    public AppointmentQueryServiceImpl(AppointmentRepository appointmentRepository) {
        this.appointmentRepository = appointmentRepository;
    }

    @Override
    public Result<List<Appointment>, AppointmentQueryFailure> handle(BranchId branchId) {
        try {
            var appointments = appointmentRepository.findByBranchId(branchId);
            return Result.success(appointments);
        } catch (IllegalArgumentException e) {
            return Result.failure(AppointmentQueryFailure.INVALID_QUERY_PARAMS);
        }
    }

    @Override
    public Result<List<Appointment>, AppointmentQueryFailure> handle(
            BranchId branchId, AppointmentStatus status) {
        try {
            var appointments = appointmentRepository.findByBranchIdAndStatus(branchId, status);
            return Result.success(appointments);
        } catch (IllegalArgumentException e) {
            return Result.failure(AppointmentQueryFailure.INVALID_QUERY_PARAMS);
        }
    }

    @Override
    public Result<Appointment, AppointmentQueryFailure> handle(UUID appointmentId) {
        return appointmentRepository.findById(appointmentId)
                .map(Result::<Appointment, AppointmentQueryFailure>success)
                .orElse(Result.failure(AppointmentQueryFailure.APPOINTMENT_NOT_FOUND));
    }

    @Override
    public Result<List<Appointment>, AppointmentQueryFailure> handle(com.forkdevs.driveos.platform.shared.domain.model.valueobjects.CustomerId customerId) {
        try {
            var appointments = appointmentRepository.findByCustomerId(customerId);
            return Result.success(appointments);
        } catch (IllegalArgumentException e) {
            return Result.failure(AppointmentQueryFailure.INVALID_QUERY_PARAMS);
        }
    }

    @Override
    public Result<List<Appointment>, AppointmentQueryFailure> handle(com.forkdevs.driveos.platform.shared.domain.model.valueobjects.VehicleId vehicleId) {
        try {
            var appointments = appointmentRepository.findByVehicleId(vehicleId);
            return Result.success(appointments);
        } catch (IllegalArgumentException e) {
            return Result.failure(AppointmentQueryFailure.INVALID_QUERY_PARAMS);
        }
    }
}
