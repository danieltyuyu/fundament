package com.forkdevs.driveos.platform.operations.interfaces.rest.resources;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

/**
 * Resource representing the request body for updating the quantity of a product in a task. This resource is used in the REST API to capture the necessary information for updating the product quantity associated with a specific task.
 * @param quantity
 * @author Joel Huamani Estefanero
 */
public record UpdateProductQuantityInTaskResource(
        @NotNull(message = "operations.error.resource.quantity.required")
        @Min(value = 1, message = "operations.error.resource.quantity.invalid")
        Integer quantity
) {}
