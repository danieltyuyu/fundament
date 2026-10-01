package com.forkdevs.driveos.platform.iot.domain.model.queries;

import com.forkdevs.driveos.platform.iot.domain.model.valueobjects.Obd2DeviceRegistrationId;

/**
 * Query representing the request to retrieve all DTC alerts for a specific OBD2 device registration.
 */
public record GetDtcAlertsByRegistrationIdQuery(
        Obd2DeviceRegistrationId obd2DeviceRegistrationId
) {
    public GetDtcAlertsByRegistrationIdQuery {
        if (obd2DeviceRegistrationId == null) {
            throw new IllegalArgumentException("obd2DeviceRegistrationId cannot be null");
        }
    }
}
