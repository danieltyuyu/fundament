package com.forkdevs.driveos.platform.fleet.interfaces.rest;

import com.forkdevs.driveos.platform.fleet.domain.model.valueobjects.EmployeeId;
import com.forkdevs.driveos.platform.shared.domain.model.valueobjects.BranchId;
import com.forkdevs.driveos.platform.fleet.application.commandservices.EmployeeRegistrationCommandFailure;
import com.forkdevs.driveos.platform.fleet.application.commandservices.EmployeeRegistrationCommandService;
import com.forkdevs.driveos.platform.fleet.application.queryservices.EmployeeRegistrationQueryService;
import com.forkdevs.driveos.platform.fleet.domain.model.commands.DeleteEmployeeRegistrationCommand;
import com.forkdevs.driveos.platform.fleet.domain.model.queries.GetEmployeeRegistrationByIdQuery;
import com.forkdevs.driveos.platform.fleet.domain.model.queries.GetEmployeeRegistrationByEmployeeIdQuery;
import com.forkdevs.driveos.platform.fleet.domain.model.queries.GetEmployeeRegistrationsByBranchIdQuery;
import com.forkdevs.driveos.platform.fleet.domain.model.queries.GetEmployeeRegistrationsByBranchIdAndStatusQuery;
import com.forkdevs.driveos.platform.fleet.domain.model.valueobjects.EmployeeRegistrationStatus;
import com.forkdevs.driveos.platform.fleet.interfaces.rest.resources.CreateEmployeeRegistrationResource;
import com.forkdevs.driveos.platform.fleet.interfaces.rest.resources.UpdateEmployeeRegistrationResource;
import com.forkdevs.driveos.platform.fleet.interfaces.rest.transform.CreateEmployeeRegistrationCommandFromResourceAssembler;
import com.forkdevs.driveos.platform.fleet.interfaces.rest.transform.EmployeeRegistrationResourceFromAggregateAssembler;
import com.forkdevs.driveos.platform.fleet.interfaces.rest.transform.UpdateEmployeeRegistrationCommandFromResourceAssembler;
import com.forkdevs.driveos.platform.shared.application.result.ApplicationError;
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
@RequestMapping(value = "/api/v1/employee-registrations", produces = "application/json")
@Tag(name = "EmployeeRegistrations", description = "Employee Registration Management Endpoints")
public class EmployeeRegistrationsController {

    private final EmployeeRegistrationCommandService commandService;
    private final EmployeeRegistrationQueryService queryService;
    private final MessageSource messageSource;

    public EmployeeRegistrationsController(EmployeeRegistrationCommandService commandService,
                                           EmployeeRegistrationQueryService queryService,
                                           MessageSource messageSource) {
        this.commandService = commandService;
        this.queryService = queryService;
        this.messageSource = messageSource;
    }

    @PostMapping
    @Operation(summary = "Create a new employee registration", description = "Creates a new employee registration")
    public ResponseEntity<?> create(@Valid @RequestBody CreateEmployeeRegistrationResource resource) {
        var command = CreateEmployeeRegistrationCommandFromResourceAssembler.toCommandFromResource(resource);
        var result = commandService.handle(command);
        return result.fold(
                registration -> ResponseEntity.status(HttpStatus.CREATED)
                        .body(EmployeeRegistrationResourceFromAggregateAssembler.toResourceFromAggregate(registration)),
                this::handleCommandFailure
        );
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get an employee registration by ID", description = "Retrieves an employee registration by ID")
    public ResponseEntity<?> getById(@PathVariable UUID id) {
        var query = new GetEmployeeRegistrationByIdQuery(new EmployeeId(id));
        var registration = queryService.handle(query);
        if (registration.isEmpty()) {
            return ResponseEntity.notFound().build();
        }
        var resource = EmployeeRegistrationResourceFromAggregateAssembler.toResourceFromAggregate(registration.get());
        return ResponseEntity.ok(resource);
    }

    @GetMapping
    @Operation(summary = "Get employee registrations", description = "Get registrations filtered by branch, branch and status, or employee ID")
    public ResponseEntity<?> getRegistrations(
            @RequestParam(required = false) UUID branchId,
            @RequestParam(required = false) String status,
            @RequestParam(required = false) UUID employeeId) {

        if (employeeId != null) {
            var query = new GetEmployeeRegistrationByEmployeeIdQuery(employeeId);
            var registration = queryService.handle(query);
            if (registration.isEmpty()) {
                return ResponseEntity.notFound().build();
            }
            return ResponseEntity.ok(EmployeeRegistrationResourceFromAggregateAssembler.toResourceFromAggregate(registration.get()));
        } else if (branchId != null && status != null) {
            var query = new GetEmployeeRegistrationsByBranchIdAndStatusQuery(new BranchId(branchId), new EmployeeRegistrationStatus(status.toUpperCase()));
            var registrations = queryService.handle(query);
            return ResponseEntity.ok(registrations.stream()
                    .map(EmployeeRegistrationResourceFromAggregateAssembler::toResourceFromAggregate)
                    .toList());
        } else if (branchId != null) {
            var query = new GetEmployeeRegistrationsByBranchIdQuery(new BranchId(branchId));
            var registrations = queryService.handle(query);
            return ResponseEntity.ok(registrations.stream()
                    .map(EmployeeRegistrationResourceFromAggregateAssembler::toResourceFromAggregate)
                    .toList());
        }

        return ResponseEntity.badRequest().build();
    }

    @PutMapping("/{id}")
    @Operation(summary = "Update an employee registration", description = "Updates the speciality and salary of an existing employee registration")
    public ResponseEntity<?> updateEmployeeRegistration(
            @PathVariable UUID id,
            @RequestBody UpdateEmployeeRegistrationResource resource) {
        
        var command = UpdateEmployeeRegistrationCommandFromResourceAssembler.toCommandFromResource(id, resource);
        var result = commandService.handle(command);
        
        return result.fold(
                registration -> ResponseEntity.ok(EmployeeRegistrationResourceFromAggregateAssembler.toResourceFromAggregate(registration)),
                this::handleCommandFailure
        );
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Deactivate an employee registration", description = "Performs a soft delete on an employee registration, marking it as inactive")
    public ResponseEntity<?> deactivateEmployeeRegistration(@PathVariable UUID id) {
        var command = new DeleteEmployeeRegistrationCommand(new EmployeeId(id));
        var result = commandService.handle(command);
        
        return result.fold(
                registration -> ResponseEntity.ok(EmployeeRegistrationResourceFromAggregateAssembler.toResourceFromAggregate(registration)),
                this::handleCommandFailure
        );
    }

    private ResponseEntity<?> handleCommandFailure(EmployeeRegistrationCommandFailure failure) {
        String messageKey = switch (failure) {
            case REGISTRATION_ALREADY_EXISTS -> "fleet.error.employeeRegistration.alreadyExists";
            case REGISTRATION_NOT_FOUND -> "fleet.error.employeeRegistration.notFound";
            case INVALID_REGISTRATION_DATA -> "fleet.error.employeeRegistration.invalidData";
        };
        String message = messageSource.getMessage(messageKey, null, LocaleContextHolder.getLocale());
        ApplicationError error = switch (failure) {
            case REGISTRATION_ALREADY_EXISTS -> ApplicationError.conflict("employeeRegistration", message);
            case REGISTRATION_NOT_FOUND -> ApplicationError.notFound("employeeRegistration", message);
            case INVALID_REGISTRATION_DATA -> ApplicationError.validationError("employeeRegistration", message);
        };
        return ErrorResponseAssembler.toErrorResponseFromApplicationError(error);
    }
}
