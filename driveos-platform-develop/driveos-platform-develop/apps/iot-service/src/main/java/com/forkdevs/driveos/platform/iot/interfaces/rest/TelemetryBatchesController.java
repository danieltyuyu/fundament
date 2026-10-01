package com.forkdevs.driveos.platform.iot.interfaces.rest;

import com.forkdevs.driveos.platform.iot.application.commandservices.TelemetryCommandService;
import com.forkdevs.driveos.platform.iot.interfaces.rest.resources.IngestTelemetryBatchResource;
import com.forkdevs.driveos.platform.iot.interfaces.rest.transform.ResponseEntityFromTelemetryCommandResultAssembler;
import com.forkdevs.driveos.platform.iot.interfaces.rest.transform.TelemetryCommandFromResourceAssembler;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.context.MessageSource;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * REST controller for managing OBD2 telemetry batch ingestion.
 */
@RestController
@RequestMapping(value = "/api/v1/telemetry-batches", produces = "application/json")
@Tag(name = "Telemetry Batches", description = "Endpoints for managing OBD2 telemetry batches")
public class TelemetryBatchesController {

    private final TelemetryCommandService commandService;
    private final MessageSource messageSource;

    public TelemetryBatchesController(TelemetryCommandService commandService, MessageSource messageSource) {
        this.commandService = commandService;
        this.messageSource = messageSource;
    }

    /**
     * Ingests a batch of telemetry snapshots from an OBD2 device.
     * @param resource the request payload containing device ID and telemetry data
     * @return a ResponseEntity containing the created snapshots or a ProblemDetail on failure
     */
    @PostMapping
    @Operation(summary = "Ingest a batch of telemetry snapshots", description = "Ingests a new batch of telemetry snapshots from an OBD2 device")
    public ResponseEntity<?> ingestTelemetryBatch(@Valid @RequestBody IngestTelemetryBatchResource resource) {
        var command = TelemetryCommandFromResourceAssembler.toCommandFromResource(resource);
        var result = commandService.handle(command);
        return ResponseEntityFromTelemetryCommandResultAssembler.toResponseEntityFromResult(result, messageSource);
    }
}
