package com.forkdevs.driveos.platform.iot.domain.model.valueobjects;

/**
 * Value object representing the status of an OBD2 Device.
 */
public record Obd2DeviceStatus(String value) {
    public Obd2DeviceStatus {
        if (value == null || value.isBlank()) {
            throw new IllegalArgumentException("OBD2 device status cannot be null or empty");
        }
    }

    public static final Obd2DeviceStatus AVAILABLE = new Obd2DeviceStatus("AVAILABLE");
    public static final Obd2DeviceStatus LINKED = new Obd2DeviceStatus("LINKED");
    public static final Obd2DeviceStatus NOT_AVAILABLE = new Obd2DeviceStatus("NOT_AVAILABLE");
}
