package com.forkdevs.driveos.platform.fleet.interfaces.rest;

import com.forkdevs.driveos.platform.fleet.application.commandservices.AppointmentCommandFailure;
import com.forkdevs.driveos.platform.fleet.application.commandservices.AppointmentCommandService;
import com.forkdevs.driveos.platform.fleet.application.queryservices.AppointmentQueryFailure;
import com.forkdevs.driveos.platform.fleet.application.queryservices.AppointmentQueryService;
import com.forkdevs.driveos.platform.fleet.domain.model.valueobjects.AppointmentStatus;
import com.forkdevs.driveos.platform.fleet.domain.model.commands.DeleteAppointmentCommand;
import com.forkdevs.driveos.platform.fleet.interfaces.rest.resources.CreateAppointmentResource;
import com.forkdevs.driveos.platform.fleet.interfaces.rest.resources.UpdateAppointmentResource;
import com.forkdevs.driveos.platform.fleet.interfaces.rest.transform.AppointmentResourceFromAggregateAssembler;
import com.forkdevs.driveos.platform.fleet.interfaces.rest.transform.CreateAppointmentCommandFromResourceAssembler;
import com.forkdevs.driveos.platform.fleet.interfaces.rest.transform.UpdateAppointmentCommandFromResourceAssembler;
import com.forkdevs.driveos.platform.shared.application.result.ApplicationError;
import com.forkdevs.driveos.platform.shared.domain.model.valueobjects.BranchId;
import com.forkdevs.driveos.platform.shared.domain.model.valueobjects.CustomerId;
import com.forkdevs.driveos.platform.shared.domain.model.valueobjects.VehicleId;
import com.forkdevs.driveos.platform.shared.interfaces.rest.transform.ErrorResponseAssembler;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping(value = "/api/v1/appointments", produces = "application/json")
@Tag(name = "Appointments", description = "Appointment Management Endpoints")
public class AppointmentsController {

        private final AppointmentCommandService commandService;
        private final AppointmentQueryService queryService;
        private final MessageSource messageSource;

        public AppointmentsController(AppointmentCommandService commandService,
                        AppointmentQueryService queryService,
                        MessageSource messageSource) {
                this.commandService = commandService;
                this.queryService = queryService;
                this.messageSource = messageSource;
        }

        @PostMapping
        @Operation(summary = "Create a new appointment", description = "Creates a new appointment")
        public ResponseEntity<?> createAppointment(@Valid @RequestBody CreateAppointmentResource resource) {
                var command = CreateAppointmentCommandFromResourceAssembler.toCommandFromResource(resource);
                var result = commandService.handle(command);
                return result.fold(
                                appointment -> ResponseEntity.status(HttpStatus.CREATED)
                                                .body(AppointmentResourceFromAggregateAssembler
                                                                .toResourceFromAggregate(appointment)),
                                this::handleCommandFailure);
        }

        @PutMapping("/{appointmentId}")
        @Operation(summary = "Update an appointment", description = "Updates an existing appointment by ID")
        public ResponseEntity<?> updateAppointment(
                        @PathVariable UUID appointmentId,
                        @Valid @RequestBody UpdateAppointmentResource resource) {
                var command = UpdateAppointmentCommandFromResourceAssembler
                                .toCommandFromResource(appointmentId, resource);
                var result = commandService.handle(command);
                return result.fold(
                                appointment -> ResponseEntity.ok(
                                                AppointmentResourceFromAggregateAssembler
                                                                .toResourceFromAggregate(appointment)),
                                this::handleCommandFailure);
        }

        @DeleteMapping("/{appointmentId}")
        @Operation(summary = "Delete an appointment", description = "Soft deletes an appointment by ID")
        public ResponseEntity<?> deleteAppointment(@PathVariable UUID appointmentId) {
                var command = new DeleteAppointmentCommand(appointmentId);
                var result = commandService.handle(command);
                return result.fold(
                                deletedId -> ResponseEntity.noContent().build(),
                                this::handleCommandFailure);
        }

        @GetMapping
        @Operation(summary = "Get appointments", description = "Get appointments filtered by branch, branch and status, customer, or vehicle ID")
        public ResponseEntity<?> getAppointments(
                        @RequestParam(required = false) UUID branchId,
                        @RequestParam(required = false) AppointmentStatus status,
                        @RequestParam(required = false) UUID customerId,
                        @RequestParam(required = false) UUID vehicleId) {

                if (branchId != null && status != null) {
                        var result = queryService.handle(new BranchId(branchId), status);
                        return result.fold(
                                        appointments -> ResponseEntity.ok(
                                                        appointments.stream()
                                                                        .map(AppointmentResourceFromAggregateAssembler::toResourceFromAggregate)
                                                                        .toList()),
                                        this::handleQueryFailure);
                } else if (branchId != null) {
                        var result = queryService.handle(new BranchId(branchId));
                        return result.fold(
                                        appointments -> ResponseEntity.ok(
                                                        appointments.stream()
                                                                        .map(AppointmentResourceFromAggregateAssembler::toResourceFromAggregate)
                                                                        .toList()),
                                        this::handleQueryFailure);
                } else if (customerId != null) {
                        var result = queryService.handle(new CustomerId(customerId));
                        return result.fold(
                                        appointments -> ResponseEntity.ok(
                                                        appointments.stream()
                                                                        .map(AppointmentResourceFromAggregateAssembler::toResourceFromAggregate)
                                                                        .toList()),
                                        this::handleQueryFailure);
                } else if (vehicleId != null) {
                        var result = queryService.handle(new VehicleId(vehicleId));
                        return result.fold(
                                        appointments -> ResponseEntity.ok(
                                                        appointments.stream()
                                                                        .map(AppointmentResourceFromAggregateAssembler::toResourceFromAggregate)
                                                                        .toList()),
                                        this::handleQueryFailure);
                }

                return handleQueryFailure(AppointmentQueryFailure.INVALID_QUERY_PARAMS);
        }

        @GetMapping("/{appointmentId}")
        @Operation(summary = "Get appointment by ID", description = "Returns the detail of a single appointment by its ID")
        public ResponseEntity<?> getById(@PathVariable UUID appointmentId) {
                var result = queryService.handle(appointmentId);
                return result.fold(
                                appointment -> ResponseEntity.ok(
                                                AppointmentResourceFromAggregateAssembler
                                                                .toResourceFromAggregate(appointment)),
                                this::handleQueryFailure);
        }

        private ResponseEntity<?> handleCommandFailure(AppointmentCommandFailure failure) {
                String messageKey = switch (failure) {
                        case APPOINTMENT_ALREADY_EXISTS -> "fleet.error.appointment.alreadyExists";
                        case APPOINTMENT_NOT_FOUND -> "fleet.error.appointment.notFound";
                        case INVALID_APPOINTMENT_DATA -> "fleet.error.appointment.invalidData";
                };
                String message = messageSource.getMessage(messageKey, null, LocaleContextHolder.getLocale());
                ApplicationError error = switch (failure) {
                        case APPOINTMENT_ALREADY_EXISTS -> ApplicationError.conflict("appointment", message);
                        case APPOINTMENT_NOT_FOUND -> ApplicationError.notFound("appointment", message);
                        case INVALID_APPOINTMENT_DATA -> ApplicationError.validationError("appointment", message);
                };
                return ErrorResponseAssembler.toErrorResponseFromApplicationError(error);
        }

        private ResponseEntity<?> handleQueryFailure(AppointmentQueryFailure failure) {
                String messageKey = switch (failure) {
                        case APPOINTMENT_NOT_FOUND -> "fleet.error.appointment.notFound";
                        case INVALID_QUERY_PARAMS -> "fleet.error.appointment.invalidQueryParams";
                };
                String message = messageSource.getMessage(messageKey, null, LocaleContextHolder.getLocale());
                ApplicationError error = switch (failure) {
                        case APPOINTMENT_NOT_FOUND -> ApplicationError.notFound("appointment", message);
                        case INVALID_QUERY_PARAMS -> ApplicationError.validationError("appointment", message);
                };
                return ErrorResponseAssembler.toErrorResponseFromApplicationError(error);
        }
}