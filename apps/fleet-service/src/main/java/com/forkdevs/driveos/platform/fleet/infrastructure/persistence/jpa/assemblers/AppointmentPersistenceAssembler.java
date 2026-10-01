package com.forkdevs.driveos.platform.fleet.infrastructure.persistence.jpa.assemblers;

import com.forkdevs.driveos.platform.fleet.domain.model.aggregates.Appointment;
import com.forkdevs.driveos.platform.fleet.infrastructure.persistence.jpa.entities.AppointmentPersistenceEntity;

public class AppointmentPersistenceAssembler {

    public static AppointmentPersistenceEntity toEntityFromAggregate(Appointment aggregate) {
        var entity = new AppointmentPersistenceEntity();

        if (aggregate.getVersion() != null) {
            entity.setId(aggregate.getId());
            entity.setVersion(aggregate.getVersion());
        }
        entity.setBranchId(aggregate.getBranchId());
        entity.setCustomerId(aggregate.getCustomerId());
        entity.setVehicleId(aggregate.getVehicleId());
        entity.setStatus(aggregate.getStatus());
        entity.setScheduledStart(aggregate.getScheduledStart());
        entity.setScheduledEnd(aggregate.getScheduledEnd());
        entity.setNotes(aggregate.getNotes());
        entity.setDeletedAt(aggregate.getDeletedAt());
        entity.setCreatedBy(aggregate.getCreatedBy());
        entity.setUpdatedBy(aggregate.getUpdatedBy());

        if (aggregate.getCreatedAt() != null) {
            entity.setCreatedAt(aggregate.getCreatedAt());
        }

        if (aggregate.getUpdatedAt() != null) {
            entity.setUpdatedAt(aggregate.getUpdatedAt());
        }

        return entity;
    }

    public static Appointment toAggregateFromEntity(AppointmentPersistenceEntity entity) {
        return new Appointment(
                entity.getId(),
                entity.getBranchId(),
                entity.getCustomerId(),
                entity.getVehicleId(),
                entity.getScheduledStart(),
                entity.getScheduledEnd(),
                entity.getStatus(),
                entity.getNotes(),
                entity.getCreatedAt(),
                entity.getUpdatedAt(),
                entity.getDeletedAt(),
                entity.getCreatedBy(),
                entity.getUpdatedBy(),
                entity.getVersion()
        );
    }
}