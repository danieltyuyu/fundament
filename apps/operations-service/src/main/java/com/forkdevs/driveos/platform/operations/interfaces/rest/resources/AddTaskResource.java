package com.forkdevs.driveos.platform.operations.interfaces.rest.resources;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.util.UUID;

/**
 * Resource representing the data required to add a new Task to a Work Order. This resource is typically used in the context of a REST API endpoint that allows clients to create new Tasks within a Work Order. It includes validation annotations to ensure that the necessary data is provided and meets certain constraints.
 * @param serviceId
 * @param assignedMechanicId
 * @param description
 * @author Joel Huamani Estefanero
 */
public record AddTaskResource(
        @NotNull(message = "operations.error.resource.serviceId.required")
        UUID serviceId,

        @NotNull(message = "operations.error.resource.assignedMechanicId.required")
        UUID assignedMechanicId,

        @NotNull(message = "operations.error.resource.description.required")
        @Size(min = 5, max = 1000, message = "operations.error.resource.description.size")
        String description
) {}