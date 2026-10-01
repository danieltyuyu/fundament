package com.forkdevs.driveos.platform.iot.interfaces.rest.resources;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

/**
 * Resource representing the request body to update a client vehicle.
 */
public record UpdateVehicleResource(
        @NotBlank(message = "Plate number is required")
        String plateNumber,

        @NotBlank(message = "Brand is required")
        String brand,

        @NotBlank(message = "Model is required")
        String model,

        @NotNull(message = "Year is required")
        @Min(value = 1886, message = "Year must be a valid manufacturing year")
        Integer year,

        @NotBlank(message = "VIN is required")
        String vin
) {
}
