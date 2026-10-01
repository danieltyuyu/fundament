package com.forkdevs.driveos.platform.iot.interfaces.rest;

import com.forkdevs.driveos.platform.iot.application.commandservices.Obd2DeviceCommandFailure;
import com.forkdevs.driveos.platform.iot.application.commandservices.Obd2DeviceCommandService;
import com.forkdevs.driveos.platform.iot.application.queryservices.Obd2DeviceQueryService;
import com.forkdevs.driveos.platform.iot.application.queryservices.TelemetryQueryService;
import com.forkdevs.driveos.platform.iot.domain.model.commands.DeleteObd2DeviceCommand;
import com.forkdevs.driveos.platform.iot.domain.model.queries.GetAvailableObd2DevicesQuery;
import com.forkdevs.driveos.platform.iot.domain.model.queries.GetLatestTelemetrySnapshotQuery;
import com.forkdevs.driveos.platform.iot.domain.model.queries.GetObd2DeviceByIdQuery;
import com.forkdevs.driveos.platform.iot.domain.model.queries.GetObd2DevicesByBranchIdQuery;
import com.forkdevs.driveos.platform.iot.domain.model.queries.GetTelemetrySnapshotHistoryQuery;
import com.forkdevs.driveos.platform.iot.domain.model.valueobjects.Obd2DeviceId;
import com.forkdevs.driveos.platform.shared.domain.model.valueobjects.BranchId;

import java.util.List;
import com.forkdevs.driveos.platform.iot.interfaces.rest.resources.CreateObd2DeviceResource;
import com.forkdevs.driveos.platform.iot.interfaces.rest.resources.Obd2DeviceResource;
import com.forkdevs.driveos.platform.iot.interfaces.rest.resources.TelemetrySnapshotResource;
import com.forkdevs.driveos.platform.iot.interfaces.rest.resources.UpdateObd2DeviceResource;
import com.forkdevs.driveos.platform.iot.interfaces.rest.transform.CreateObd2DeviceCommandFromResourceAssembler;
import com.forkdevs.driveos.platform.iot.interfaces.rest.transform.Obd2DeviceResourceFromAggregateAssembler;
import com.forkdevs.driveos.platform.iot.interfaces.rest.transform.ResponseEntityFromObd2DeviceCommandResultAssembler;
import com.forkdevs.driveos.platform.iot.interfaces.rest.transform.TelemetrySnapshotResourceFromAggregateAssembler;
import com.forkdevs.driveos.platform.iot.interfaces.rest.transform.UpdateObd2DeviceCommandFromResourceAssembler;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.context.MessageSource;
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import org.springframework.context.i18n.LocaleContextHolder;
import java.util.UUID;

/**
 * REST controller for managing OBD2 Devices registration.
 */
@RestController
@RequestMapping(value = "/api/v1/obd2-devices", produces = "application/json")
@Tag(name = "OBD2 Devices", description = "Endpoints for managing OBD2 device fleet")
public class Obd2DevicesController {

    private final Obd2DeviceCommandService commandService;
    private final Obd2DeviceQueryService queryService;
    private final TelemetryQueryService telemetryQueryService;
    private final MessageSource messageSource;

    public Obd2DevicesController(
            Obd2DeviceCommandService commandService,
            Obd2DeviceQueryService queryService,
            TelemetryQueryService telemetryQueryService,
            MessageSource messageSource
    ) {
        this.commandService = commandService;
        this.queryService = queryService;
        this.telemetryQueryService = telemetryQueryService;
        this.messageSource = messageSource;
    }

    /**
     * Registers a new OBD2 device.
     * @param resource the request payload containing branch ID and MAC address
     * @return a ResponseEntity containing the created resource or localized error details
     */
    @PostMapping
    @Operation(summary = "Register a new OBD2 device", description = "Registers a new physical OBD2 device in the specified branch")
    public ResponseEntity<?> createObd2Device(@Valid @RequestBody CreateObd2DeviceResource resource) {
        var command = CreateObd2DeviceCommandFromResourceAssembler.toCommandFromResource(resource);
        var result = commandService.handle(command);
        return ResponseEntityFromObd2DeviceCommandResultAssembler.toResponseEntityFromResult(result, messageSource);
    }

    /**
     * Retrieves the details of a registered OBD2 device by its unique ID.
     * @param id the unique identifier of the OBD2 device
     * @return a ResponseEntity containing the device details or 404 if not found
     */
    @GetMapping("/{id}")
    @Operation(summary = "Get OBD2 device by ID", description = "Retrieves the details of a registered OBD2 device by its unique ID")
    public ResponseEntity<Obd2DeviceResource> getObd2DeviceById(@PathVariable UUID id) {
        var query = new GetObd2DeviceByIdQuery(new Obd2DeviceId(id));
        var obd2Device = queryService.handle(query);
        return obd2Device
                .map(device -> ResponseEntity.ok(Obd2DeviceResourceFromAggregateAssembler.toResourceFromAggregate(device)))
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    /**
     * Deletes (unregisters) an OBD2 device.
     * @param id the unique identifier of the OBD2 device
     * @return 204 No Content on success, or a ProblemDetail on failure
     */
    @DeleteMapping("/{id}")
    @Operation(summary = "Delete an OBD2 device", description = "Performs a soft delete of an OBD2 device by its unique ID")
    public ResponseEntity<?> deleteObd2Device(@PathVariable UUID id) {
        var command = new DeleteObd2DeviceCommand(new Obd2DeviceId(id));
        var result = commandService.handle(command);

        return result.fold(
                _void -> ResponseEntity.noContent().build(),
                failure -> {
                    HttpStatus status = switch (failure) {
                        case Obd2DeviceCommandFailure.NotFound(String _) -> HttpStatus.NOT_FOUND;
                        case Obd2DeviceCommandFailure.InvalidState(String _) -> HttpStatus.BAD_REQUEST;
                        case Obd2DeviceCommandFailure.Duplicate(String _) -> HttpStatus.CONFLICT;
                    };
                    String messageKey = switch (failure) {
                        case Obd2DeviceCommandFailure.NotFound(String message) -> message;
                        case Obd2DeviceCommandFailure.InvalidState(String message) -> message;
                        case Obd2DeviceCommandFailure.Duplicate(String message) -> message;
                    };
                    String localizedMessage;
                    try {
                        localizedMessage = messageSource.getMessage(messageKey, null, LocaleContextHolder.getLocale());
                    } catch (Exception e) {
                        localizedMessage = messageKey;
                    }
                    return ResponseEntity.status(status).body(
                            ProblemDetail.forStatusAndDetail(status, localizedMessage)
                    );
                }
        );
    }

    /**
     * Updates an existing OBD2 device.
     * @param id the unique identifier of the OBD2 device
     * @param resource the update request body containing new MAC address
     * @return a ResponseEntity containing the updated device resource or localized error details
     */
    @PutMapping("/{id}")
    @Operation(summary = "Update an OBD2 device", description = "Updates an existing registered OBD2 device's details (such as MAC address)")
    public ResponseEntity<?> updateObd2Device(@PathVariable UUID id, @Valid @RequestBody UpdateObd2DeviceResource resource) {
        var command = UpdateObd2DeviceCommandFromResourceAssembler.toCommandFromResource(id, resource);
        var result = commandService.handle(command);
        return ResponseEntityFromObd2DeviceCommandResultAssembler.toResponseEntityFromResult(result, HttpStatus.OK, messageSource);
    }

    /**
     * Retrieves all registered OBD2 devices under a branch, optionally filtered by status.
     * Use ?status=available to retrieve only unlinked devices.
     * @param branchId the branch identifier to filter devices
     * @param status optional status filter (e.g. "available")
     * @return a ResponseEntity containing the list of device resources
     */
    @GetMapping
    @Operation(summary = "Get OBD2 devices by branch", description = "Retrieves all registered OBD2 devices under a specific branch. Use ?status=available to filter by availability.")
    public ResponseEntity<List<Obd2DeviceResource>> getObd2Devices(
            @RequestParam UUID branchId,
            @RequestParam(required = false) String status
    ) {
        List<?> list;
        if ("available".equalsIgnoreCase(status)) {
            var query = new GetAvailableObd2DevicesQuery(new BranchId(branchId));
            list = queryService.handle(query);
        } else {
            var query = new GetObd2DevicesByBranchIdQuery(new BranchId(branchId));
            list = queryService.handle(query);
        }
        var resources = list.stream()
                .map(device -> Obd2DeviceResourceFromAggregateAssembler.toResourceFromAggregate(
                        (com.forkdevs.driveos.platform.iot.domain.model.aggregates.Obd2Device) device))
                .toList();
        return ResponseEntity.ok(resources);
    }

    /**
     * Retrieves the most recent telemetry snapshot for a specific OBD2 device.
     * @param deviceId the unique identifier of the OBD2 device
     * @return a ResponseEntity containing the latest snapshot or 404 ProblemDetail if not found
     */
    @GetMapping("/{deviceId}/telemetry-snapshots/latest")
    @Operation(summary = "Get latest telemetry snapshot for a device", description = "Retrieves the most recent telemetry capture from a specific OBD2 device")
    public ResponseEntity<?> getLatestTelemetrySnapshot(@PathVariable UUID deviceId) {
        var query = new GetLatestTelemetrySnapshotQuery(new Obd2DeviceId(deviceId));
        var result = telemetryQueryService.handle(query);

        return result
                .map(snapshot -> ResponseEntity.ok(
                        TelemetrySnapshotResourceFromAggregateAssembler.toResourceFromAggregate(snapshot)))
                .<ResponseEntity<?>>map(r -> r)
                .orElseGet(() -> {
                    var status = HttpStatus.NOT_FOUND;
                    return ResponseEntity.status(status).body(
                            ProblemDetail.forStatusAndDetail(status, "No telemetry snapshot found for device " + deviceId)
                    );
                });
    }

    /**
     * Retrieves all historical telemetry snapshots for a specific OBD2 device.
     * @param deviceId the unique identifier of the OBD2 device
     * @return a ResponseEntity containing the list of telemetry snapshots ordered descending by date
     */
    @GetMapping("/{deviceId}/telemetry-snapshots")
    @Operation(summary = "Get telemetry snapshot history for a device", description = "Retrieves all telemetry snapshots recorded for a specific OBD2 device ordered descending by date")
    public ResponseEntity<List<TelemetrySnapshotResource>> getTelemetrySnapshotHistory(@PathVariable UUID deviceId) {
        var query = new GetTelemetrySnapshotHistoryQuery(new Obd2DeviceId(deviceId));
        var list = telemetryQueryService.handle(query);
        var resources = list.stream()
                .map(TelemetrySnapshotResourceFromAggregateAssembler::toResourceFromAggregate)
                .toList();
        return ResponseEntity.ok(resources);
    }
}
