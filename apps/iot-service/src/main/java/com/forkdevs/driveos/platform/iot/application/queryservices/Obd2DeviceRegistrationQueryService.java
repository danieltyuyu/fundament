package com.forkdevs.driveos.platform.iot.application.queryservices;

import com.forkdevs.driveos.platform.iot.domain.model.aggregates.Obd2DeviceRegistration;
import com.forkdevs.driveos.platform.iot.domain.model.queries.GetObd2DeviceRegistrationsByBranchIdAndStatusQuery;

import java.util.List;

/**
 * Service interface for handling OBD2 device registration queries.
 */
public interface Obd2DeviceRegistrationQueryService {

    /**
     * Handles retrieving OBD2 device registrations by branch and status.
     * @param query the query containing branch ID and registration status
     * @return the list of matching registrations
     */
    List<Obd2DeviceRegistration> handle(GetObd2DeviceRegistrationsByBranchIdAndStatusQuery query);
}
