package com.forkdevs.driveos.platform.iot.application.queryservices;

import com.forkdevs.driveos.platform.iot.domain.model.aggregates.Obd2Device;
import com.forkdevs.driveos.platform.iot.domain.model.queries.GetAvailableObd2DevicesQuery;
import com.forkdevs.driveos.platform.iot.domain.model.queries.GetObd2DeviceByIdQuery;
import com.forkdevs.driveos.platform.iot.domain.model.queries.GetObd2DevicesByBranchIdQuery;

import java.util.List;
import java.util.Optional;

/**
 * Service interface for handling OBD2 device queries.
 */
public interface Obd2DeviceQueryService {

    /**
     * Handles retrieving an OBD2 device by its unique ID.
     * @param query the query containing the device ID
     * @return an Optional containing the device if found, or empty
     */
    Optional<Obd2Device> handle(GetObd2DeviceByIdQuery query);

    /**
     * Handles retrieving all OBD2 devices registered in a specific branch.
     * @param query the query containing the branch ID
     * @return the list of registered OBD2 devices
     */
    List<Obd2Device> handle(GetObd2DevicesByBranchIdQuery query);

    /**
     * Handles retrieving all available (unlinked) OBD2 devices in a branch.
     * @param query the query containing the branch ID
     * @return the list of available OBD2 devices
     */
    List<Obd2Device> handle(GetAvailableObd2DevicesQuery query);
}
