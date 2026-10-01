package com.forkdevs.driveos.platform.iot.interfaces.rest.resources;

import java.util.UUID;

/**
 * Resource representing an OBD2 Device returned in REST responses.
 */
public record Obd2DeviceResource(
        UUID id,
        UUID branchId,
        String macAddress,
        String status
) {
}
