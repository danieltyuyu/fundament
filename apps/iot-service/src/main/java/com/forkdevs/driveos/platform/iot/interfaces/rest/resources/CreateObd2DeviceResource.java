package com.forkdevs.driveos.platform.iot.interfaces.rest.resources;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import java.util.UUID;

/**
 * Resource representing the request body to register a new OBD2 Device.
 */
public record CreateObd2DeviceResource(
        @NotNull(message = "Branch ID is required")
        UUID branchId,

        @NotBlank(message = "MAC address is required")
        @Pattern(regexp = "^([0-9A-Fa-f]{2}[:-]){5}([0-9A-Fa-f]{2})$", message = "Invalid MAC address format")
        String macAddress
) {
}
