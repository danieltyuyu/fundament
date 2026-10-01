package com.forkdevs.driveos.platform.iot.application.commandservices;

import com.forkdevs.driveos.platform.iot.domain.model.aggregates.TelemetrySnapshot;
import com.forkdevs.driveos.platform.iot.domain.model.commands.IngestTelemetryBatchCommand;
import com.forkdevs.driveos.platform.shared.application.result.Result;

import java.util.List;

/**
 * Service interface for handling telemetry ingestion commands.
 */
public interface TelemetryCommandService {

    /**
     * Handles the ingestion of a batch of telemetry snapshots.
     * @param command the command containing device details and snapshots list
     * @return a Result containing the list of successfully saved snapshots, or a TelemetryCommandFailure
     */
    Result<List<TelemetrySnapshot>, TelemetryCommandFailure> handle(IngestTelemetryBatchCommand command);
}
