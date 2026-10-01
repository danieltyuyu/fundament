package com.forkdevs.driveos.platform.iot.application.queryservices;

import com.forkdevs.driveos.platform.iot.domain.model.aggregates.TelemetrySnapshot;
import com.forkdevs.driveos.platform.iot.domain.model.queries.GetLatestTelemetrySnapshotQuery;
import com.forkdevs.driveos.platform.iot.domain.model.queries.GetTelemetrySnapshotHistoryQuery;
import com.forkdevs.driveos.platform.iot.domain.model.queries.GetTelemetrySnapshotsByRegistrationIdQuery;
import com.forkdevs.driveos.platform.iot.domain.model.queries.GetVehicleTelemetrySnapshotHistoryQuery;

import java.util.List;
import java.util.Optional;

/**
 * Service interface for handling telemetry queries.
 */
public interface TelemetryQueryService {

    /**
     * Handles retrieving the latest telemetry snapshot.
     * @param query the query containing the device identifier
     * @return an Optional containing the latest snapshot if found
     */
    Optional<TelemetrySnapshot> handle(GetLatestTelemetrySnapshotQuery query);

    /**
     * Handles retrieving the complete history of telemetry snapshots.
     * @param query the query containing the device identifier
     * @return the list of snapshots ordered by creation date descending
     */
    List<TelemetrySnapshot> handle(GetTelemetrySnapshotHistoryQuery query);

    /**
     * Handles retrieving the complete history of telemetry snapshots for a specific OBD2 device registration.
     * @param query the query containing the registration identifier
     * @return the list of snapshots ordered by creation date descending
     */
    List<TelemetrySnapshot> handle(GetTelemetrySnapshotsByRegistrationIdQuery query);

    /**
     * Handles retrieving the historical telemetry snapshots for a vehicle starting from its active driver registration start date.
     * @param query the query containing the vehicle identifier
     * @return the list of snapshots ordered by creation date descending
     */
    List<TelemetrySnapshot> handle(GetVehicleTelemetrySnapshotHistoryQuery query);
}
