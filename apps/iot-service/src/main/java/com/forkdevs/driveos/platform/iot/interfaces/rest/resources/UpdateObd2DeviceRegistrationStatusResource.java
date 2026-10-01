package com.forkdevs.driveos.platform.iot.interfaces.rest.resources;

import jakarta.validation.constraints.NotBlank;

/**
 * Request payload for updating the status of an OBD2 device registration.
 */
public record UpdateObd2DeviceRegistrationStatusResource(
        @NotBlank String status
) {}
