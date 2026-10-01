package com.forkdevs.driveos.platform.operations.interfaces.rest;

import com.forkdevs.driveos.platform.operations.application.commandservices.ServiceCommandService;
import com.forkdevs.driveos.platform.operations.application.queryservices.ServiceQueryService;
import com.forkdevs.driveos.platform.operations.domain.model.commands.DeleteServiceCommand;
import com.forkdevs.driveos.platform.operations.domain.model.queries.GetAllServicesByBranchIdQuery;
import com.forkdevs.driveos.platform.operations.domain.model.valueobjects.ServiceId;
import com.forkdevs.driveos.platform.operations.interfaces.rest.resources.CreateServiceResource;
import com.forkdevs.driveos.platform.operations.interfaces.rest.resources.ServiceResource;
import com.forkdevs.driveos.platform.operations.interfaces.rest.resources.UpdateServiceResource;
import com.forkdevs.driveos.platform.operations.interfaces.rest.transform.CreateServiceCommandFromResourceAssembler;
import com.forkdevs.driveos.platform.operations.interfaces.rest.transform.ServiceResourceFromEntityAssembler;
import com.forkdevs.driveos.platform.operations.interfaces.rest.transform.UpdateServiceCommandFromResourceAssembler;
import com.forkdevs.driveos.platform.shared.domain.model.valueobjects.BranchId;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@RestController
@RequestMapping(value = "/api/v1/services", produces = MediaType.APPLICATION_JSON_VALUE)
@Tag(name = "Services", description = "Services Management Endpoints")
public class ServicesController {

    private final ServiceCommandService serviceCommandService;
    private final ServiceQueryService serviceQueryService;

    public ServicesController(ServiceCommandService serviceCommandService, ServiceQueryService serviceQueryService) {
        this.serviceCommandService = serviceCommandService;
        this.serviceQueryService = serviceQueryService;
    }

    @Operation(summary = "Create a new service", description = "Creates a new service with the provided details")
    @PostMapping
    public ResponseEntity<ServiceResource> createService(@RequestBody CreateServiceResource resource) {
        var command = CreateServiceCommandFromResourceAssembler.toCommandFromResource(resource);
        var service = serviceCommandService.handle(command);
        if (service.isEmpty()) {
            return ResponseEntity.badRequest().build();
        }

        var serviceResource = ServiceResourceFromEntityAssembler.toResourceFromEntity(service.get());
        return new ResponseEntity<>(serviceResource, HttpStatus.CREATED);
    }

    @Operation(summary = "Update a service", description = "Updates an existing service using the service ID")
    @PutMapping("/{serviceId}")
    public ResponseEntity<ServiceResource> updateService(@PathVariable UUID serviceId, @RequestBody UpdateServiceResource resource) {
        var command = UpdateServiceCommandFromResourceAssembler.toCommandFromResource(serviceId, resource);
        var service = serviceCommandService.handle(command);
        if (service.isEmpty()) {
            return ResponseEntity.badRequest().build();
        }

        var serviceResource = ServiceResourceFromEntityAssembler.toResourceFromEntity(service.get());
        return ResponseEntity.ok(serviceResource);
    }

    @Operation(summary = "Delete a service", description = "Deletes an existing service using the service ID")
    @DeleteMapping("/{serviceId}")
    public ResponseEntity<?> deleteService(@PathVariable UUID serviceId) {
        var command = new DeleteServiceCommand(new ServiceId(serviceId));
        serviceCommandService.handle(command);
        return ResponseEntity.ok().build();
    }

    @Operation(summary = "Get services by branch ID", description = "Retrieves all services belonging to a specific branch")
    @GetMapping
    public ResponseEntity<List<ServiceResource>> getServicesByBranchId(@RequestParam(name = "branchId") UUID branchId) {
        var query = new GetAllServicesByBranchIdQuery(new BranchId(branchId));
        var services = serviceQueryService.handle(query);

        var serviceResources = services.stream()
                .map(ServiceResourceFromEntityAssembler::toResourceFromEntity)
                .collect(Collectors.toList());

        return ResponseEntity.ok(serviceResources);
    }
}
