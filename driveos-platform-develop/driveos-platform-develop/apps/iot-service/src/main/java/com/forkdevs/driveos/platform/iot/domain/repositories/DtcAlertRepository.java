package com.forkdevs.driveos.platform.iot.domain.repositories;

import com.forkdevs.driveos.platform.iot.domain.model.aggregates.DtcAlert;
import com.forkdevs.driveos.platform.iot.domain.model.valueobjects.Obd2DeviceRegistrationId;

import java.time.Instant;
import java.util.List;

/**
 * Domain repository interface/port for DtcAlert aggregates.
 */
public interface DtcAlertRepository {

    /**
     * Finds all DTC alerts for a specific OBD2 device registration.
     * @param registrationId the registration ID
     * @return the list of DTC alerts ordered by creation date descending
     */
    List<DtcAlert> findAllByRegistrationId(Obd2DeviceRegistrationId registrationId);

    /**
     * Finds all DTC alerts for a specific OBD2 device registration starting from a specific timestamp.
     * @param registrationId the registration ID
     * @param startTimestamp the start timestamp
     * @return the list of DTC alerts ordered by creation date descending
     */
    List<DtcAlert> findAllByRegistrationIdAndCreatedAtGreaterThanEqual(
            Obd2DeviceRegistrationId registrationId,
            Instant startTimestamp
    );
}
