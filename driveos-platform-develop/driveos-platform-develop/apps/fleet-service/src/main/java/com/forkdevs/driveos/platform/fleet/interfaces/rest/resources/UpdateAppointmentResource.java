package com.forkdevs.driveos.platform.fleet.interfaces.rest.resources;

import jakarta.validation.constraints.NotNull;

import java.time.LocalDateTime;
import java.util.UUID;

import com.forkdevs.driveos.platform.fleet.domain.model.valueobjects.AppointmentStatus;

public record UpdateAppointmentResource(
        @NotNull(message = "fleet.error.resource.branchId.required") UUID branchId,
        @NotNull(message = "fleet.error.resource.customerId.required") UUID customerId,
        @NotNull(message = "fleet.error.resource.vehicleId.required") UUID vehicleId,
        @NotNull(message = "fleet.error.resource.scheduledStart.required") LocalDateTime scheduledStart,
        @NotNull(message = "fleet.error.resource.status.required") AppointmentStatus status,
        String notes
) {
}