package com.forkdevs.driveos.platform.fleet.interfaces.rest.resources;

import java.time.LocalDateTime;
import java.util.UUID;

public record AppointmentResource(
        UUID id,
        UUID branchId,
        UUID customerId,
        UUID vehicleId,
        LocalDateTime scheduledStart,
        LocalDateTime scheduledEnd,
        String status,
        String notes
) {
}