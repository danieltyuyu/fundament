package com.forkdevs.driveos.platform.operations.interfaces.rest.resources;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

/**
 * Resource representing the details required to update a Work Order, including the diagnostic summary and mileage information. This resource is used in RESTful API endpoints to receive and validate the necessary data for updating a Work Order's details.
 * @param diagnosticSummary
 * @param mileageIn
 * @author Joel Huamani Estefanero
 */
public record UpdateWorkOrderDetailsResource(
        @NotNull(message = "operations.error.resource.diagnosticSummary.required")
        @Size(min = 5, max = 2000, message = "operations.error.resource.diagnosticSummary.size")
        String diagnosticSummary,
        @NotNull(message = "operations.error.resource.mileageIn.required")
        Integer mileageIn
) {}
