package com.forkdevs.driveos.platform.operations.interfaces.rest.resources;

import jakarta.validation.constraints.NotNull;
import java.util.UUID;

/**
 * Resource representing the necessary information to add a Product to a Task within a Work Order. This resource is typically used in REST API endpoints to receive the data required for adding a Product to a Task.
 * @param productId
 * @param quantity
 * @author Joel Huamani Estefanero
 */
public record AddProductResource(
        @NotNull(message = "operations.error.resource.productId.required")
        UUID productId,

        @NotNull(message = "operations.error.resource.quantity.required")
        Integer quantity
) {}