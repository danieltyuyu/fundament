package com.forkdevs.driveos.platform.fleet.interfaces.rest;

import com.forkdevs.driveos.platform.fleet.application.commandservices.CustomerRegistrationCommandFailure;
import com.forkdevs.driveos.platform.fleet.application.commandservices.CustomerRegistrationCommandService;
import com.forkdevs.driveos.platform.fleet.application.queryservices.CustomerRegistrationQueryFailure;
import com.forkdevs.driveos.platform.fleet.application.queryservices.CustomerRegistrationQueryService;
import com.forkdevs.driveos.platform.fleet.domain.model.queries.GetCustomerRegistrationByCustomerIdQuery;
import com.forkdevs.driveos.platform.fleet.domain.model.valueobjects.CustomerRegistrationStatus;
import com.forkdevs.driveos.platform.fleet.domain.model.commands.DeleteCustomerRegistrationCommand;
import com.forkdevs.driveos.platform.fleet.interfaces.rest.resources.CreateCustomerRegistrationResource;
import com.forkdevs.driveos.platform.fleet.interfaces.rest.resources.UpdateCustomerRegistrationResource;
import com.forkdevs.driveos.platform.fleet.interfaces.rest.transform.CustomerRegistrationResourceFromAggregateAssembler;
import com.forkdevs.driveos.platform.fleet.interfaces.rest.transform.CreateCustomerRegistrationCommandFromResourceAssembler;
import com.forkdevs.driveos.platform.fleet.interfaces.rest.transform.UpdateCustomerRegistrationCommandFromResourceAssembler;
import com.forkdevs.driveos.platform.shared.application.result.ApplicationError;
import com.forkdevs.driveos.platform.shared.domain.model.valueobjects.BranchId;
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
@RequestMapping(value = "/api/v1/customer-registrations", produces = "application/json")
@Tag(name = "CustomerRegistrations", description = "Customer Registration Management Endpoints")
public class CustomerRegistrationsController {

    private final CustomerRegistrationCommandService commandService;
    private final CustomerRegistrationQueryService queryService;
    private final MessageSource messageSource;

    public CustomerRegistrationsController(CustomerRegistrationCommandService commandService,
                                           CustomerRegistrationQueryService queryService,
                                           MessageSource messageSource) {
        this.commandService = commandService;
        this.queryService = queryService;
        this.messageSource = messageSource;
    }

    @PostMapping
    @Operation(summary = "Create a new customer registration", description = "Creates a new customer registration")
    public ResponseEntity<?> create(@Valid @RequestBody CreateCustomerRegistrationResource resource) {
        var command = CreateCustomerRegistrationCommandFromResourceAssembler.toCommandFromResource(resource);
        var result = commandService.handle(command);
        return result.fold(
                registration -> ResponseEntity.status(HttpStatus.CREATED)
                        .body(CustomerRegistrationResourceFromAggregateAssembler.toResourceFromAggregate(registration)),
                this::handleCommandFailure
        );
    }

    @PutMapping("/{registrationId}")
    @Operation(summary = "Update a customer registration", description = "Updates an existing customer registration by ID (e.g., change status)")
    public ResponseEntity<?> update(@PathVariable UUID registrationId,
                                    @Valid @RequestBody UpdateCustomerRegistrationResource resource) {
        var command = UpdateCustomerRegistrationCommandFromResourceAssembler.toCommandFromResource(registrationId, resource);
        var result = commandService.handle(command);
        return result.fold(
                registration -> ResponseEntity.ok(CustomerRegistrationResourceFromAggregateAssembler.toResourceFromAggregate(registration)),
                this::handleCommandFailure
        );
    }

    @DeleteMapping("/{registrationId}")
    @Operation(summary = "Delete (deactivate) a customer registration", description = "Soft-deactivates a customer registration by ID")
    public ResponseEntity<?> delete(@PathVariable UUID registrationId) {
        var command = new DeleteCustomerRegistrationCommand(registrationId);
        var result = commandService.handle(command);
        return result.fold(
                id -> ResponseEntity.noContent().build(),
                this::handleCommandFailure
        );
    }

    @GetMapping
    @Operation(summary = "Get registrations", description = "Get registrations filtered by branch, branch and status, or customer ID")
    public ResponseEntity<?> getRegistrations(
            @RequestParam(required = false) UUID branchId,
            @RequestParam(required = false) CustomerRegistrationStatus status,
            @RequestParam(required = false) UUID customerId) {

        if (customerId != null) {
            var result = queryService.handle(new GetCustomerRegistrationByCustomerIdQuery(customerId));
            return result.fold(
                    reg -> ResponseEntity.ok(CustomerRegistrationResourceFromAggregateAssembler.toResourceFromAggregate(reg)),
                    this::handleQueryFailure
            );
        } else if (branchId != null && status != null) {
            var result = queryService.handle(new BranchId(branchId), status);
            return result.fold(
                    regs -> ResponseEntity.ok(regs.stream()
                            .map(CustomerRegistrationResourceFromAggregateAssembler::toResourceFromAggregate).toList()),
                    this::handleQueryFailure
            );
        } else if (branchId != null) {
            var result = queryService.handle(new BranchId(branchId));
            return result.fold(
                    regs -> ResponseEntity.ok(regs.stream()
                            .map(CustomerRegistrationResourceFromAggregateAssembler::toResourceFromAggregate).toList()),
                    this::handleQueryFailure
            );
        }

        return handleQueryFailure(CustomerRegistrationQueryFailure.INVALID_QUERY_PARAMS);
    }

    @GetMapping("/{registrationId}")
    @Operation(summary = "Get registration by ID", description = "Returns the detail of a single registration by its ID")
    public ResponseEntity<?> getById(@PathVariable UUID registrationId) {
        var result = queryService.handle(registrationId);
        return result.fold(
                reg -> ResponseEntity.ok(CustomerRegistrationResourceFromAggregateAssembler.toResourceFromAggregate(reg)),
                this::handleQueryFailure
        );
    }



    private ResponseEntity<?> handleCommandFailure(CustomerRegistrationCommandFailure failure) {
        String messageKey = switch (failure) {
            case REGISTRATION_ALREADY_EXISTS -> "fleet.error.customerRegistration.alreadyExists";
            case REGISTRATION_NOT_FOUND -> "fleet.error.customerRegistration.notFound";
            case INVALID_REGISTRATION_DATA -> "fleet.error.customerRegistration.invalidData";
        };
        String message = messageSource.getMessage(messageKey, null, LocaleContextHolder.getLocale());
        var error = switch (failure) {
            case REGISTRATION_ALREADY_EXISTS -> ApplicationError.conflict("customerRegistration", message);
            case REGISTRATION_NOT_FOUND -> ApplicationError.notFound("customerRegistration", message);
            case INVALID_REGISTRATION_DATA -> ApplicationError.validationError("customerRegistration", message);
        };
        return ErrorResponseAssembler.toErrorResponseFromApplicationError(error);
    }

    private ResponseEntity<?> handleQueryFailure(CustomerRegistrationQueryFailure failure) {
        String messageKey = switch (failure) {
            case REGISTRATION_NOT_FOUND -> "fleet.error.customerRegistration.notFound";
            case INVALID_QUERY_PARAMS -> "fleet.error.customerRegistration.invalidQueryParams";
        };
        String message = messageSource.getMessage(messageKey, null, LocaleContextHolder.getLocale());
        var error = switch (failure) {
            case REGISTRATION_NOT_FOUND -> ApplicationError.notFound("customerRegistration", message);
            case INVALID_QUERY_PARAMS -> ApplicationError.validationError("customerRegistration", message);
        };
        return ErrorResponseAssembler.toErrorResponseFromApplicationError(error);
    }
}

