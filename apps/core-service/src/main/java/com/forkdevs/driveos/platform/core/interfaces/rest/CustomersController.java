package com.forkdevs.driveos.platform.core.interfaces.rest;

import com.forkdevs.driveos.platform.shared.domain.model.valueobjects.CustomerId;
import com.forkdevs.driveos.platform.core.domain.model.valueobjects.UserId;

import com.forkdevs.driveos.platform.core.domain.model.queries.GetCustomerByIdQuery;
import com.forkdevs.driveos.platform.core.domain.model.queries.GetCustomerByUserIdQuery;
import com.forkdevs.driveos.platform.core.domain.model.commands.DeleteCustomerCommand;
import com.forkdevs.driveos.platform.core.application.commandservices.CustomerCommandService;
import com.forkdevs.driveos.platform.core.application.queryservices.CustomerQueryService;
import com.forkdevs.driveos.platform.core.interfaces.rest.resources.CreateCustomerResource;
import com.forkdevs.driveos.platform.core.interfaces.rest.resources.CustomerResource;
import com.forkdevs.driveos.platform.core.interfaces.rest.resources.UpdateCustomerResource;
import com.forkdevs.driveos.platform.core.interfaces.rest.transform.CreateCustomerCommandFromResourceAssembler;
import com.forkdevs.driveos.platform.core.interfaces.rest.transform.CustomerResourceFromEntityAssembler;
import com.forkdevs.driveos.platform.core.interfaces.rest.transform.UpdateCustomerCommandFromResourceAssembler;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping(value = "/api/v1/customers", produces = MediaType.APPLICATION_JSON_VALUE)
@Tag(name = "Customers", description = "Customer Management Endpoints")
public class CustomersController {

    private final CustomerCommandService customerCommandService;
    private final CustomerQueryService customerQueryService;

    public CustomersController(CustomerCommandService customerCommandService, CustomerQueryService customerQueryService) {
        this.customerCommandService = customerCommandService;
        this.customerQueryService = customerQueryService;
    }

    @Operation(summary = "Create a new customer profile", description = "Creates a new customer profile associated with a user ID")
    @PostMapping
    public ResponseEntity<CustomerResource> createCustomer(@RequestBody CreateCustomerResource resource) {
        var command = CreateCustomerCommandFromResourceAssembler.toCommandFromResource(resource);
        var customer = customerCommandService.handle(command);
        if (customer.isEmpty()) {
            return ResponseEntity.badRequest().build();
        }

        var customerResource = CustomerResourceFromEntityAssembler.toResourceFromEntity(customer.get());
        return new ResponseEntity<>(customerResource, HttpStatus.CREATED);
    }

    @Operation(summary = "Update a customer profile", description = "Updates an existing customer profile")
    @PutMapping("/{customerId}")
    public ResponseEntity<CustomerResource> updateCustomer(@PathVariable UUID customerId, @RequestBody UpdateCustomerResource resource) {
        var command = UpdateCustomerCommandFromResourceAssembler.toCommandFromResource(customerId, resource);
        var customer = customerCommandService.handle(command);
        if (customer.isEmpty()) {
            return ResponseEntity.badRequest().build();
        }

        var customerResource = CustomerResourceFromEntityAssembler.toResourceFromEntity(customer.get());
        return ResponseEntity.ok(customerResource);
    }

    @Operation(summary = "Get a customer profile by ID", description = "Retrieves the details of a specific customer profile")
    @GetMapping("/{customerId}")
    public ResponseEntity<CustomerResource> getCustomerById(@PathVariable UUID customerId) {
        var query = new GetCustomerByIdQuery(new CustomerId(customerId));
        var customer = customerQueryService.handle(query);
        if (customer.isEmpty()) {
            return ResponseEntity.notFound().build();
        }

        var customerResource = CustomerResourceFromEntityAssembler.toResourceFromEntity(customer.get());
        return ResponseEntity.ok(customerResource);
    }

    @Operation(summary = "Get a customer profile by User ID", description = "Retrieves the details of a specific customer profile using the User ID")
    @GetMapping
    public ResponseEntity<CustomerResource> getCustomerByUserId(@RequestParam(name = "userId") UUID userId) {
        var query = new GetCustomerByUserIdQuery(new UserId(userId));
        var customer = customerQueryService.handle(query);
        if (customer.isEmpty()) {
            return ResponseEntity.notFound().build();
        }

        var customerResource = CustomerResourceFromEntityAssembler.toResourceFromEntity(customer.get());
        return ResponseEntity.ok(customerResource);
    }

    @Operation(summary = "Delete a customer profile", description = "Deletes an existing customer profile")
    @DeleteMapping("/{customerId}")
    public ResponseEntity<?> deleteCustomer(@PathVariable UUID customerId) {
        var command = new DeleteCustomerCommand(new CustomerId(customerId));
        customerCommandService.handle(command);
        return ResponseEntity.ok().build();
    }

}

