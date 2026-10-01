package com.forkdevs.driveos.platform.fleet.infrastructure.persistence.jpa.repositories;

import com.forkdevs.driveos.platform.fleet.domain.model.valueobjects.AppointmentStatus;
import com.forkdevs.driveos.platform.fleet.infrastructure.persistence.jpa.entities.AppointmentPersistenceEntity;
import com.forkdevs.driveos.platform.shared.domain.model.valueobjects.BranchId;
import com.forkdevs.driveos.platform.shared.domain.model.valueobjects.CustomerId;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

public interface AppointmentJpaRepository extends JpaRepository<AppointmentPersistenceEntity, UUID> {

        boolean existsByScheduledStartLessThanAndScheduledEndGreaterThan(
                        LocalDateTime scheduledEnd, LocalDateTime scheduledStart);

        boolean existsByIdNotAndScheduledStartLessThanAndScheduledEndGreaterThan(
                        UUID appointmentId, LocalDateTime scheduledEnd, LocalDateTime scheduledStart);

        List<AppointmentPersistenceEntity> findByBranchId(BranchId branchId);

        List<AppointmentPersistenceEntity> findByCustomerId(CustomerId customerId);

        List<AppointmentPersistenceEntity> findByVehicleId(com.forkdevs.driveos.platform.shared.domain.model.valueobjects.VehicleId vehicleId);

        List<AppointmentPersistenceEntity> findByBranchIdAndStatus(
                        BranchId branchId, AppointmentStatus status);
}