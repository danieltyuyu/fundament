package com.forkdevs.driveos.platform.core.interfaces.rest;

import com.forkdevs.driveos.platform.core.domain.model.valueobjects.OwnerId;
import com.forkdevs.driveos.platform.core.domain.model.valueobjects.WorkshopId;

import com.forkdevs.driveos.platform.core.domain.model.queries.GetAllWorkshopsByOwnerIdQuery;
import com.forkdevs.driveos.platform.core.domain.model.queries.GetWorkshopByIdQuery;
import com.forkdevs.driveos.platform.core.application.commandservices.WorkshopCommandService;
import com.forkdevs.driveos.platform.core.application.queryservices.WorkshopQueryService;
import com.forkdevs.driveos.platform.core.interfaces.rest.resources.CreateWorkshopResource;
import com.forkdevs.driveos.platform.core.interfaces.rest.resources.UpdateWorkshopResource;
import com.forkdevs.driveos.platform.core.interfaces.rest.resources.WorkshopResource;
import com.forkdevs.driveos.platform.core.interfaces.rest.transform.CreateWorkshopCommandFromResourceAssembler;
import com.forkdevs.driveos.platform.core.interfaces.rest.transform.UpdateWorkshopCommandFromResourceAssembler;
import com.forkdevs.driveos.platform.core.interfaces.rest.transform.WorkshopResourceFromEntityAssembler;
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
@RequestMapping(value = "/api/v1/workshops", produces = MediaType.APPLICATION_JSON_VALUE)
@Tag(name = "Workshops", description = "Workshop Management Endpoints")
public class WorkshopsController {

    private final WorkshopCommandService workshopCommandService;
    private final WorkshopQueryService workshopQueryService;

    public WorkshopsController(WorkshopCommandService workshopCommandService, WorkshopQueryService workshopQueryService) {
        this.workshopCommandService = workshopCommandService;
        this.workshopQueryService = workshopQueryService;
    }

    @Operation(summary = "Create a new workshop", description = "Creates a new workshop")
    @PostMapping
    public ResponseEntity<WorkshopResource> createWorkshop(@RequestBody CreateWorkshopResource resource) {
        var command = CreateWorkshopCommandFromResourceAssembler.toCommandFromResource(resource);
        var workshop = workshopCommandService.handle(command);
        if (workshop.isEmpty()) {
            return ResponseEntity.badRequest().build();
        }

        var workshopResource = WorkshopResourceFromEntityAssembler.toResourceFromEntity(workshop.get());
        return new ResponseEntity<>(workshopResource, HttpStatus.CREATED);
    }

    @Operation(summary = "Update an existing workshop", description = "Updates the details of a workshop by its ID")
    @PutMapping("/{workshopId}")
    public ResponseEntity<WorkshopResource> updateWorkshop(@PathVariable UUID workshopId, @RequestBody UpdateWorkshopResource resource) {
        var command = UpdateWorkshopCommandFromResourceAssembler.toCommandFromResource(workshopId, resource);
        var workshop = workshopCommandService.handle(command);
        if (workshop.isEmpty()) {
            return ResponseEntity.badRequest().build();
        }

        var workshopResource = WorkshopResourceFromEntityAssembler.toResourceFromEntity(workshop.get());
        return ResponseEntity.ok(workshopResource);
    }

    @Operation(summary = "Get a workshop by ID", description = "Retrieves the details of a specific workshop")
    @GetMapping("/{workshopId}")
    public ResponseEntity<WorkshopResource> getWorkshopById(@PathVariable UUID workshopId) {
        var query = new GetWorkshopByIdQuery(new WorkshopId(workshopId));
        var workshop = workshopQueryService.handle(query);
        if (workshop.isEmpty()) {
            return ResponseEntity.notFound().build();
        }

        var workshopResource = WorkshopResourceFromEntityAssembler.toResourceFromEntity(workshop.get());
        return ResponseEntity.ok(workshopResource);
    }

    @Operation(summary = "Get workshops by owner ID", description = "Retrieves all workshops belonging to a specific owner")
    @GetMapping
    public ResponseEntity<List<WorkshopResource>> getWorkshopsByOwnerId(@RequestParam(name = "ownerId") UUID ownerId) {
        var query = new GetAllWorkshopsByOwnerIdQuery(new OwnerId(ownerId));
        var workshops = workshopQueryService.handle(query);
        
        var workshopResources = workshops.stream()
                .map(WorkshopResourceFromEntityAssembler::toResourceFromEntity)
                .collect(Collectors.toList());
                
        return ResponseEntity.ok(workshopResources);
    }
}
