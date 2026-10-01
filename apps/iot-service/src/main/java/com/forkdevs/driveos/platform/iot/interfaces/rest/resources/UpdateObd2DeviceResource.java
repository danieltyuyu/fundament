package com.forkdevs.driveos.platform.iot.interfaces.rest.resources;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;

/**
 * Resource representing the request body to update an OBD2 Device.
 */
public record UpdateObd2DeviceResource(
        @NotBlank(message = "MAC address is required")
        @Pattern(regexp = "^([0-9A-Fa-f]{2}[:-]){5}([0-9A-Fa-f]{2})$", message = "Invalid MAC address format")
        String macAddress
) {
}
