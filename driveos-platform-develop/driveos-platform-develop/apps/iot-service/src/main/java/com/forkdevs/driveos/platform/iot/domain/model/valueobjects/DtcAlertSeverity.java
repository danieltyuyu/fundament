package com.forkdevs.driveos.platform.iot.domain.model.valueobjects;

import java.util.Set;

/**
 * Value object representing the severity of a DTC Alert.
 */
public record DtcAlertSeverity(String value) {

    private static final Set<String> VALID_SEVERITIES = Set.of("LOW", "MEDIUM", "HIGH", "CRITICAL");

    public DtcAlertSeverity {
        if (value == null || value.isBlank()) {
            throw new IllegalArgumentException("DTC alert severity cannot be null or empty");
        }
        if (!VALID_SEVERITIES.contains(value.toUpperCase())) {
            throw new IllegalArgumentException("Invalid DTC alert severity: " + value);
        }
    }

    public static final DtcAlertSeverity LOW = new DtcAlertSeverity("LOW");
    public static final DtcAlertSeverity MEDIUM = new DtcAlertSeverity("MEDIUM");
    public static final DtcAlertSeverity HIGH = new DtcAlertSeverity("HIGH");
    public static final DtcAlertSeverity CRITICAL = new DtcAlertSeverity("CRITICAL");
}
