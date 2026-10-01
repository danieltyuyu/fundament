package com.forkdevs.driveos.platform.fleet.infrastructure.persistence.jpa.adapters;

import com.forkdevs.driveos.platform.fleet.domain.model.aggregates.Appointment;
import com.forkdevs.driveos.platform.fleet.domain.model.valueobjects.AppointmentStatus;
import com.forkdevs.driveos.platform.fleet.domain.repositories.AppointmentRepository;
import com.forkdevs.driveos.platform.fleet.infrastructure.persistence.jpa.assemblers.AppointmentPersistenceAssembler;
import com.forkdevs.driveos.platform.fleet.infrastructure.persistence.jpa.repositories.AppointmentJpaRepository;
import com.forkdevs.driveos.platform.shared.domain.model.valueobjects.BranchId;
import com.forkdevs.driveos.platform.shared.domain.model.valueobjects.CustomerId;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public class AppointmentRepositoryAdapter implements AppointmentRepository {

    private final AppointmentJpaRepository appointmentJpaRepository;

    public AppointmentRepositoryAdapter(AppointmentJpaRepository appointmentJpaRepository) {
        this.appointmentJpaRepository = appointmentJpaRepository;
    }

    @Override
    public Appointment save(Appointment appointment) {
        var entity = AppointmentPersistenceAssembler.toEntityFromAggregate(appointment);
        var savedEntity = appointmentJpaRepository.save(entity);
        return AppointmentPersistenceAssembler.toAggregateFromEntity(savedEntity);
    }

    @Override
    public Optional<Appointment> findById(UUID appointmentId) {
        return appointmentJpaRepository.findById(appointmentId)
                .map(AppointmentPersistenceAssembler::toAggregateFromEntity);
    }

    @Override
    public boolean existsById(UUID appointmentId) {
        return appointmentJpaRepository.existsById(appointmentId);
    }

    @Override
    public void deleteById(UUID appointmentId) {
        appointmentJpaRepository.findById(appointmentId)
                .ifPresent(appointmentJpaRepository::delete);
    }

    @Override
    public boolean existsByScheduledStartLessThanAndScheduledEndGreaterThan(
            LocalDateTime scheduledEnd, LocalDateTime scheduledStart) {
        return appointmentJpaRepository
                .existsByScheduledStartLessThanAndScheduledEndGreaterThan(scheduledEnd, scheduledStart);
    }

    @Override
    public boolean existsByIdNotAndScheduledStartLessThanAndScheduledEndGreaterThan(
            UUID appointmentId, LocalDateTime scheduledEnd, LocalDateTime scheduledStart) {
        return appointmentJpaRepository
                .existsByIdNotAndScheduledStartLessThanAndScheduledEndGreaterThan(
                        appointmentId, scheduledEnd, scheduledStart);
    }

    @Override
    public List<Appointment> findByBranchId(BranchId branchId) {
        return appointmentJpaRepository.findByBranchId(branchId)
                .stream()
                .map(AppointmentPersistenceAssembler::toAggregateFromEntity)
                .toList();
    }

    @Override
    public List<Appointment> findByCustomerId(CustomerId customerId) {
        return appointmentJpaRepository.findByCustomerId(customerId)
                .stream()
                .map(AppointmentPersistenceAssembler::toAggregateFromEntity)
                .toList();
    }

    @Override
    public List<Appointment> findByVehicleId(com.forkdevs.driveos.platform.shared.domain.model.valueobjects.VehicleId vehicleId) {
        return appointmentJpaRepository.findByVehicleId(vehicleId)
                .stream()
                .map(AppointmentPersistenceAssembler::toAggregateFromEntity)
                .toList();
    }

    @Override
    public List<Appointment> findByBranchIdAndStatus(BranchId branchId, AppointmentStatus status) {
        return appointmentJpaRepository.findByBranchIdAndStatus(branchId, status)
                .stream()
                .map(AppointmentPersistenceAssembler::toAggregateFromEntity)
                .toList();
    }
}