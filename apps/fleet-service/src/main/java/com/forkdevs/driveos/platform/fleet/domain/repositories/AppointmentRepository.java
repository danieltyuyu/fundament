package com.forkdevs.driveos.platform.fleet.domain.repositories;

import com.forkdevs.driveos.platform.fleet.domain.model.aggregates.Appointment;
import com.forkdevs.driveos.platform.fleet.domain.model.valueobjects.AppointmentStatus;
import com.forkdevs.driveos.platform.shared.domain.model.valueobjects.BranchId;
import com.forkdevs.driveos.platform.shared.domain.model.valueobjects.CustomerId;
import com.forkdevs.driveos.platform.shared.domain.model.valueobjects.VehicleId;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface AppointmentRepository {

    Appointment save(Appointment appointment);
    Optional<Appointment> findById(UUID appointmentId);
    boolean existsById(UUID appointmentId);
    void deleteById(UUID appointmentId);
    boolean existsByScheduledStartLessThanAndScheduledEndGreaterThan(
            LocalDateTime scheduledEnd, LocalDateTime scheduledStart);
    boolean existsByIdNotAndScheduledStartLessThanAndScheduledEndGreaterThan(
            UUID appointmentId, LocalDateTime scheduledEnd, LocalDateTime scheduledStart);

    List<Appointment> findByBranchId(BranchId branchId);

    List<Appointment> findByCustomerId(CustomerId customerId);
    
    List<Appointment> findByVehicleId(VehicleId vehicleId);

    List<Appointment> findByBranchIdAndStatus(BranchId branchId, AppointmentStatus status);
}