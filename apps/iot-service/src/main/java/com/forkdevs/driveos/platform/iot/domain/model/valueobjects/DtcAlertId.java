package com.forkdevs.driveos.platform.iot.domain.model.valueobjects;

import java.util.UUID;

/**
 * Value object representing the unique identifier of a DTC Alert in the system.
 * @param value The UUID value representing the DTC Alert ID. It must not be null.
 */
public record DtcAlertId(UUID value) {

    private static final String NOT_NULL_UUID_REGEX = "iot.error.dtcAlertId.required";

    /**
     * Constructs a DtcAlertId value object with validation to ensure the UUID is not null.
     * @param value the UUID value representing the DTC Alert ID
     * @throws IllegalArgumentException if the UUID value is null
     */
    public DtcAlertId {
        if (value == null) {
            throw new IllegalArgumentException(NOT_NULL_UUID_REGEX);
        }
    }

    /**
     * Static factory method to generate a new DtcAlertId with a random UUID.
     * @return a new DtcAlertId with a random UUID
     */
    public static DtcAlertId random() {
        return new DtcAlertId(UUID.randomUUID());
    }
}
