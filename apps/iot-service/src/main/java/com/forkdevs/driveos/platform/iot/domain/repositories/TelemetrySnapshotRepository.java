package com.forkdevs.driveos.platform.iot.domain.repositories;

import com.forkdevs.driveos.platform.iot.domain.model.aggregates.TelemetrySnapshot;
import com.forkdevs.driveos.platform.iot.domain.model.valueobjects.Obd2DeviceRegistrationId;
import com.forkdevs.driveos.platform.iot.domain.model.valueobjects.TelemetrySnapshotId;

import java.time.Instant;
import java.util.List;
import java.util.Optional;

/**
 * Domain repository interface for managing {@link TelemetrySnapshot} aggregates.
 */
public interface TelemetrySnapshotRepository {

    /**
     * Saves a single TelemetrySnapshot aggregate.
     * @param telemetrySnapshot the aggregate to save
     * @return the saved aggregate
     */
    TelemetrySnapshot save(TelemetrySnapshot telemetrySnapshot);

    /**
     * Saves multiple TelemetrySnapshot aggregates.
     * @param telemetrySnapshots the list of aggregates to save
     * @return the list of saved aggregates
     */
    List<TelemetrySnapshot> saveAll(List<TelemetrySnapshot> telemetrySnapshots);

    /**
     * Finds a TelemetrySnapshot by its unique identifier.
     * @param id the unique identifier of the snapshot
     * @return an Optional containing the aggregate if found
     */
    Optional<TelemetrySnapshot> findById(TelemetrySnapshotId id);

    /**
     * Finds the latest TelemetrySnapshot for a given device registration.
     * @param registrationId the unique identifier of the registration
     * @return an Optional containing the latest snapshot if found
     */
    Optional<TelemetrySnapshot> findLatestByRegistrationId(Obd2DeviceRegistrationId registrationId);

    /**
     * Finds the complete history of TelemetrySnapshots for a given device registration.
     * @param registrationId the unique identifier of the registration
     * @return the list of snapshots ordered by creation date descending
     */
    List<TelemetrySnapshot> findAllByRegistrationId(Obd2DeviceRegistrationId registrationId);

    /**
     * Finds all TelemetrySnapshots for a given device registration starting from a specific timestamp.
     * @param registrationId the unique identifier of the registration
     * @param startTimestamp the start timestamp
     * @return the list of snapshots ordered by creation date descending
     */
    List<TelemetrySnapshot> findAllByRegistrationIdAndCreatedAtGreaterThanEqual(
            Obd2DeviceRegistrationId registrationId,
            Instant startTimestamp
    );
}
