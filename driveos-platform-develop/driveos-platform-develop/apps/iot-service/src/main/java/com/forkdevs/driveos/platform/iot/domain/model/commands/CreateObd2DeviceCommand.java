package com.forkdevs.driveos.platform.iot.domain.model.commands;
import com.forkdevs.driveos.platform.shared.domain.model.valueobjects.BranchId;

/**
 * Command to register a new OBD2 device.
 */
public record CreateObd2DeviceCommand(
        BranchId branchId,
        String macAddress
) {
}
