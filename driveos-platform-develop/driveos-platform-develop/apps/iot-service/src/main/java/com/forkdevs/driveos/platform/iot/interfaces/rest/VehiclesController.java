package com.forkdevs.driveos.platform.iot.interfaces.rest;

import com.forkdevs.driveos.platform.iot.application.commandservices.VehicleCommandService;
import com.forkdevs.driveos.platform.iot.application.queryservices.DtcAlertQueryService;
import com.forkdevs.driveos.platform.iot.application.queryservices.TelemetryQueryService;
import com.forkdevs.driveos.platform.iot.application.queryservices.VehicleQueryService;
import com.forkdevs.driveos.platform.iot.domain.model.queries.GetVehiclesAvailableForLinkingQuery;
import com.forkdevs.driveos.platform.iot.domain.model.queries.GetVehicleDtcAlertHistoryQuery;
import com.forkdevs.driveos.platform.iot.domain.model.queries.GetVehicleTelemetrySnapshotHistoryQuery;
import com.forkdevs.driveos.platform.iot.domain.model.queries.GetVehicleByIdQuery;
import com.forkdevs.driveos.platform.iot.interfaces.rest.resources.DtcAlertResource;
import com.forkdevs.driveos.platform.iot.interfaces.rest.resources.RegisterVehicleResource;
import com.forkdevs.driveos.platform.iot.interfaces.rest.resources.TelemetrySnapshotResource;
import com.forkdevs.driveos.platform.iot.interfaces.rest.resources.UpdateVehicleResource;
import com.forkdevs.driveos.platform.iot.interfaces.rest.transform.DtcAlertResourceFromAggregateAssembler;
import com.forkdevs.driveos.platform.iot.interfaces.rest.transform.RegisterVehicleCommandFromResourceAssembler;
import com.forkdevs.driveos.platform.iot.interfaces.rest.transform.ResponseEntityFromVehicleCommandResultAssembler;
import com.forkdevs.driveos.platform.iot.interfaces.rest.transform.TelemetrySnapshotResourceFromAggregateAssembler;
import com.forkdevs.driveos.platform.iot.interfaces.rest.transform.UpdateVehicleCommandFromResourceAssembler;
import com.forkdevs.driveos.platform.iot.domain.model.commands.DeleteVehicleCommand;
import com.forkdevs.driveos.platform.iot.interfaces.rest.transform.VehicleResourceFromAggregateAssembler;
import com.forkdevs.driveos.platform.shared.domain.model.valueobjects.BranchId;
import com.forkdevs.driveos.platform.shared.domain.model.valueobjects.VehicleId;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.context.MessageSource;
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

/**
 * REST controller for managing Vehicle operations inside the iot context.
 */
@RestController
@RequestMapping(value = "/api/v1/vehicles", produces = "application/json")
@Tag(name = "Vehicles", description = "Endpoints for managing vehicle fleet inside the iot context")
public class VehiclesController {

    private final VehicleQueryService vehicleQueryService;
    private final VehicleCommandService vehicleCommandService;
    private final TelemetryQueryService telemetryQueryService;
    private final DtcAlertQueryService dtcAlertQueryService;
    private final MessageSource messageSource;

    public VehiclesController(VehicleQueryService vehicleQueryService,
                              VehicleCommandService vehicleCommandService,
                              TelemetryQueryService telemetryQueryService,
                              DtcAlertQueryService dtcAlertQueryService,
                              MessageSource messageSource) {
        this.vehicleQueryService = vehicleQueryService;
        this.vehicleCommandService = vehicleCommandService;
        this.telemetryQueryService = telemetryQueryService;
        this.dtcAlertQueryService = dtcAlertQueryService;
        this.messageSource = messageSource;
    }

    /**
     * Retrieves vehicles under a specific branch, filtered by status.
     * @param branchId the branch identifier to filter vehicles
     * @param status the vehicle status filter (e.g. "available-for-linking")
     * @return a ResponseEntity containing the list of vehicle resources, or a ProblemDetail on unsupported status
     */
    @GetMapping
    @Operation(summary = "Get vehicles by branch and status", description = "Retrieves vehicles under a specific branch. Use ?status=available-for-linking to get vehicles available for OBD2 device linking.")
    public ResponseEntity<?> getVehicles(
            @RequestParam UUID branchId,
            @RequestParam String status) {

        if ("available-for-linking".equalsIgnoreCase(status)) {
            var query = new GetVehiclesAvailableForLinkingQuery(new BranchId(branchId));
            var list = vehicleQueryService.handle(query);
            var resources = list.stream()
                    .map(VehicleResourceFromAggregateAssembler::toResourceFromAggregate)
                    .toList();
            return ResponseEntity.ok(resources);
        }

        var httpStatus = HttpStatus.valueOf(422);
        return ResponseEntity.status(httpStatus).body(
                ProblemDetail.forStatusAndDetail(httpStatus, "Unsupported vehicle status filter: " + status)
        );
    }

    /**
     * Retrieves client vehicle details by its unique identifier.
     * @param id the unique identifier of the vehicle
     * @return a ResponseEntity containing the VehicleResource
     */
    @GetMapping("/{id}")
    @Operation(summary = "Get client vehicle by ID", description = "Retrieves client vehicle details by its unique identifier")
    public ResponseEntity<?> getVehicleById(@PathVariable UUID id) {
        var query = new GetVehicleByIdQuery(new VehicleId(id));
        var vehicleOpt = vehicleQueryService.handle(query);
        if (vehicleOpt.isEmpty()) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(VehicleResourceFromAggregateAssembler.toResourceFromAggregate(vehicleOpt.get()));
    }

    /**
     * Registers a new vehicle and associates its registration with the authenticated user.
     * @param resource the vehicle registration request details DTO
     * @param authentication the authenticated user token context
     * @return a ResponseEntity containing the created VehicleRegistrationResource
     */
    @PostMapping
    @Operation(summary = "Register a client vehicle", description = "Registers a client vehicle and links it to the authenticated user")
    public ResponseEntity<?> registerVehicle(
            @Valid @RequestBody RegisterVehicleResource resource,
            Authentication authentication) {
        UUID userId = null;
        if (authentication != null && authentication.getPrincipal() != null) {
            Object principal = authentication.getPrincipal();
            if (principal instanceof com.forkdevs.driveos.platform.auth.domain.model.AuthenticatedUser authUser) {
                userId = authUser.getId();
            } else {
                try {
                    var method = principal.getClass().getMethod("getId");
                    Object res = method.invoke(principal);
                    if (res instanceof UUID uuid) {
                        userId = uuid;
                    } else if (res instanceof String str) {
                        userId = UUID.fromString(str);
                    }
                } catch (Exception ignored) {
                }
            }
        }

        var command = RegisterVehicleCommandFromResourceAssembler.toCommandFromResource(userId, resource);
        var result = vehicleCommandService.handle(command);

        return ResponseEntityFromVehicleCommandResultAssembler.toResponseEntityFromRegistrationResult(result, messageSource);
    }

    /**
     * Updates client vehicle details.
     * @param id the unique identifier of the vehicle
     * @param resource the vehicle update request details DTO
     * @return a ResponseEntity containing the updated VehicleResource
     */
    @PutMapping("/{id}")
    @Operation(summary = "Update client vehicle", description = "Updates client vehicle details by its unique identifier")
    public ResponseEntity<?> updateVehicle(
            @PathVariable UUID id,
            @Valid @RequestBody UpdateVehicleResource resource) {
        var command = UpdateVehicleCommandFromResourceAssembler.toCommandFromResource(id, resource);
        var result = vehicleCommandService.handle(command);

        return ResponseEntityFromVehicleCommandResultAssembler.toResponseEntityFromVehicleResult(result, messageSource);
    }

    /**
     * Deletes (soft deletes) a vehicle by its unique identifier.
     * @param id the unique identifier of the vehicle
     * @return 204 No Content on success, or a ProblemDetail on failure
     */
    @DeleteMapping("/{id}")
    @Operation(summary = "Delete client vehicle", description = "Performs a soft delete of a vehicle and deactivates active driver/OBD2 links")
    public ResponseEntity<?> deleteVehicle(@PathVariable UUID id) {
        var command = new DeleteVehicleCommand(new VehicleId(id));
        var result = vehicleCommandService.handle(command);
        return ResponseEntityFromVehicleCommandResultAssembler.toResponseEntityFromVoidResult(result, messageSource);
    }

    /**
     * Retrieves telemetry snapshots for a vehicle starting from the start of its active driver registration.
     * @param vehicleId the vehicle identifier
     * @return the list of telemetry snapshots
     */
    @GetMapping("/{vehicleId}/telemetry-snapshots")
    @Operation(summary = "Get historical telemetry snapshots for vehicle", description = "Retrieves all telemetry snapshots captured for the vehicle since its active registration start date")
    public ResponseEntity<List<TelemetrySnapshotResource>> getVehicleTelemetrySnapshots(@PathVariable UUID vehicleId) {
        var query = new GetVehicleTelemetrySnapshotHistoryQuery(new VehicleId(vehicleId));
        var list = telemetryQueryService.handle(query);
        var resources = list.stream()
                .map(TelemetrySnapshotResourceFromAggregateAssembler::toResourceFromAggregate)
                .toList();
        return ResponseEntity.ok(resources);
    }

    /**
     * Retrieves historical DTC alerts for a vehicle starting from the start of its active driver registration.
     * @param vehicleId the vehicle identifier
     * @return the list of DTC alerts
     */
    @GetMapping("/{vehicleId}/dtc-alerts")
    @Operation(summary = "Get historical DTC alerts for vehicle", description = "Retrieves all DTC/motor alerts captured for the vehicle since its active registration start date")
    public ResponseEntity<List<DtcAlertResource>> getVehicleDtcAlerts(@PathVariable UUID vehicleId) {
        var query = new GetVehicleDtcAlertHistoryQuery(new VehicleId(vehicleId));
        var list = dtcAlertQueryService.handle(query);
        var resources = list.stream()
                .map(DtcAlertResourceFromAggregateAssembler::toResourceFromAggregate)
                .toList();
        return ResponseEntity.ok(resources);
    }
}
