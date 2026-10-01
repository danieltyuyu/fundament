package com.forkdevs.driveos.platform.operations.interfaces.rest.resources;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.util.UUID;

/**
 * Resource representing the necessary information to create a new Work Order in the system. This resource is typically used as the payload for a REST API endpoint that handles the creation of Work Orders. It includes fields such as appointmentId, branchId, vehicleId, customerId, internalNumber, diagnosticSummary, and mileageIn, all of which are required for the successful creation of a Work Order.
 * @param appointmentId
 * @param branchId
 * @param vehicleId
 * @param customerId
 * @param diagnosticSummary
 * @param mileageIn
 * @author Joel Huamani Estefanero
 */
public record CreateWorkOrderResource(
        @NotNull(message = "operations.error.resource.appointmentId.required")
        UUID appointmentId,

        @NotNull(message = "operations.error.resource.branchId.required")
        UUID branchId,

        @NotNull(message = "operations.error.resource.vehicleId.required")
        UUID vehicleId,

        @NotNull(message = "operations.error.resource.customerId.required")
        UUID customerId,

        @NotNull(message = "operations.error.resource.diagnosticSummary.required")
        @Size(min = 5, max = 2000, message = "operations.error.resource.diagnosticSummary.size")
        String diagnosticSummary,

        @NotNull(message = "operations.error.resource.mileageIn.required")
        Integer mileageIn
) {}