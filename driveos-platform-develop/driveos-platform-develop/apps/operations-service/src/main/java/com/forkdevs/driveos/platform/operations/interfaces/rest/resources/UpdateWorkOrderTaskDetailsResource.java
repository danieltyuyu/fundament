package com.forkdevs.driveos.platform.operations.interfaces.rest.resources;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.util.UUID;

/**
 * Resource representing the details required to update a Task within a Work Order. This resource is typically used in REST API endpoints to receive the necessary information from the client when updating a Task's details, such as the associated Service, assigned Mechanic, description of the work, and labor price.
 * @param serviceId
 * @param assignedMechanicId
 * @param description
 * @author Joel Huamani Estefanero
 */
public record UpdateWorkOrderTaskDetailsResource(
        @NotNull(message = "operations.error.resource.serviceId.required")
        UUID serviceId,

        @NotNull(message = "operations.error.resource.assignedMechanicId.required")
        UUID assignedMechanicId,

        @NotNull(message = "operations.error.resource.description.required")
        @Size(min = 5, max = 1000, message = "operations.error.resource.description.size")
        String description
) {}
