package com.forkdevs.driveos.platform.fleet.interfaces.rest.resources;

import jakarta.validation.constraints.NotNull;

import java.time.LocalDateTime;
import java.util.UUID;

public record CreateAppointmentResource(
        @NotNull(message = "fleet.error.resource.branchId.required") UUID branchId,
        @NotNull(message = "fleet.error.resource.customerId.required") UUID customerId,
        @NotNull(message = "fleet.error.resource.vehicleId.required") UUID vehicleId,
        @NotNull(message = "fleet.error.resource.scheduledStart.required") LocalDateTime scheduledStart,
        String notes
) {
}