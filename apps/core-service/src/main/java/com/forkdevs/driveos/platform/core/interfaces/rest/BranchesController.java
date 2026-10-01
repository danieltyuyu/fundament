package com.forkdevs.driveos.platform.core.interfaces.rest;

import com.forkdevs.driveos.platform.shared.domain.model.valueobjects.BranchId;
import com.forkdevs.driveos.platform.core.domain.model.valueobjects.WorkshopId;

import com.forkdevs.driveos.platform.core.domain.model.queries.GetAllBranchesByWorkshopIdQuery;
import com.forkdevs.driveos.platform.core.domain.model.queries.GetBranchByIdQuery;
import com.forkdevs.driveos.platform.core.domain.model.queries.GetIssuerTaxIdByBranchIdQuery;
import com.forkdevs.driveos.platform.core.application.commandservices.BranchCommandService;
import com.forkdevs.driveos.platform.core.application.commandservices.SubscriptionCommandService;
import com.forkdevs.driveos.platform.core.application.queryservices.BranchQueryService;
import com.forkdevs.driveos.platform.core.domain.model.commands.CancelSubscriptionCommand;
import com.forkdevs.driveos.platform.core.interfaces.rest.resources.AssignSubscriptionResource;
import com.forkdevs.driveos.platform.core.interfaces.rest.resources.BranchResource;
import com.forkdevs.driveos.platform.core.interfaces.rest.resources.BranchSubscriptionResource;
import com.forkdevs.driveos.platform.core.interfaces.rest.resources.CreateBranchResource;
import com.forkdevs.driveos.platform.core.interfaces.rest.resources.IssuerTaxIdResource;
import com.forkdevs.driveos.platform.core.interfaces.rest.resources.UpdateBranchResource;
import com.forkdevs.driveos.platform.core.interfaces.rest.transform.AssignSubscriptionCommandFromResourceAssembler;
import com.forkdevs.driveos.platform.core.interfaces.rest.transform.BranchResourceFromEntityAssembler;
import com.forkdevs.driveos.platform.core.interfaces.rest.transform.BranchSubscriptionResourceFromEntityAssembler;
import com.forkdevs.driveos.platform.core.interfaces.rest.transform.CreateBranchCommandFromResourceAssembler;
import com.forkdevs.driveos.platform.core.interfaces.rest.transform.UpdateBranchCommandFromResourceAssembler;
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
@RequestMapping(value = "/api/v1/branches", produces = MediaType.APPLICATION_JSON_VALUE)
@Tag(name = "Branches", description = "Branch Management Endpoints")
public class BranchesController {

    private final BranchCommandService branchCommandService;
    private final BranchQueryService branchQueryService;
    private final SubscriptionCommandService subscriptionCommandService;

    public BranchesController(
            BranchCommandService branchCommandService, 
            BranchQueryService branchQueryService,
            SubscriptionCommandService subscriptionCommandService) {
        this.branchCommandService = branchCommandService;
        this.branchQueryService = branchQueryService;
        this.subscriptionCommandService = subscriptionCommandService;
    }

    @Operation(summary = "Create a new branch", description = "Creates a new branch associated with a specific workshop")
    @PostMapping
    public ResponseEntity<BranchResource> createBranch(@RequestBody CreateBranchResource resource) {
        var command = CreateBranchCommandFromResourceAssembler.toCommandFromResource(resource);
        var branch = branchCommandService.handle(command);
        if (branch.isEmpty()) {
            return ResponseEntity.badRequest().build();
        }

        var branchResource = BranchResourceFromEntityAssembler.toResourceFromEntity(branch.get());
        return new ResponseEntity<>(branchResource, HttpStatus.CREATED);
    }

    @Operation(summary = "Update an existing branch", description = "Updates the details of a branch by its ID")
    @PutMapping("/{branchId}")
    public ResponseEntity<BranchResource> updateBranch(@PathVariable UUID branchId, @RequestBody UpdateBranchResource resource) {
        var command = UpdateBranchCommandFromResourceAssembler.toCommandFromResource(branchId, resource);
        var branch = branchCommandService.handle(command);
        if (branch.isEmpty()) {
            return ResponseEntity.badRequest().build();
        }

        var branchResource = BranchResourceFromEntityAssembler.toResourceFromEntity(branch.get());
        return ResponseEntity.ok(branchResource);
    }

    @Operation(summary = "Get a branch by ID", description = "Retrieves the details of a specific branch")
    @GetMapping("/{branchId}")
    public ResponseEntity<BranchResource> getBranchById(@PathVariable UUID branchId) {
        var query = new GetBranchByIdQuery(new BranchId(branchId));
        var branch = branchQueryService.handle(query);
        if (branch.isEmpty()) {
            return ResponseEntity.notFound().build();
        }

        var branchResource = BranchResourceFromEntityAssembler.toResourceFromEntity(branch.get());
        return ResponseEntity.ok(branchResource);
    }

    @Operation(summary = "Get branches by workshop ID", description = "Retrieves all branches belonging to a specific workshop")
    @GetMapping
    public ResponseEntity<List<BranchResource>> getBranchesByWorkshopId(@RequestParam(name = "workshopId") UUID workshopId) {
        var query = new GetAllBranchesByWorkshopIdQuery(new WorkshopId(workshopId));
        var branches = branchQueryService.handle(query);

        var branchResources = branches.stream()
                .map(BranchResourceFromEntityAssembler::toResourceFromEntity)
                .collect(Collectors.toList());

        return ResponseEntity.ok(branchResources);
    }

    @Operation(summary = "Simulate payment and assign subscription", description = "Simulates a payment (Mock Stripe) using a dummy credit card and assigns the subscription plan")
    @PostMapping("/{branchId}/subscriptions")
    public ResponseEntity<BranchSubscriptionResource> assignSubscription(
            @PathVariable UUID branchId, 
            @RequestBody AssignSubscriptionResource resource) {
        var command = AssignSubscriptionCommandFromResourceAssembler.toCommandFromResource(branchId, resource);
        var subscription = subscriptionCommandService.handle(command);
        if (subscription.isEmpty()) {
            return ResponseEntity.badRequest().build();
        }

        var subscriptionResource = BranchSubscriptionResourceFromEntityAssembler.toResourceFromEntity(subscription.get());
        return new ResponseEntity<>(subscriptionResource, HttpStatus.CREATED);
    }

    @Operation(summary = "Cancel an active subscription", description = "Cancels the currently active subscription of a branch")
    @DeleteMapping("/{branchId}/subscription")
    public ResponseEntity<BranchSubscriptionResource> cancelSubscription(@PathVariable UUID branchId) {
        var command = new CancelSubscriptionCommand(new BranchId(branchId));
        var subscription = subscriptionCommandService.handle(command);
        if (subscription.isEmpty()) {
            return ResponseEntity.badRequest().build();
        }

        var subscriptionResource = BranchSubscriptionResourceFromEntityAssembler.toResourceFromEntity(subscription.get());
        return ResponseEntity.ok(subscriptionResource);
    }

    @Operation(summary = "Get issuer tax ID by branch ID", description = "Retrieves the workshop tax ID (RUC) for the given branch, used for billing and invoice issuing")
    @GetMapping("/{branchId}/issuer-tax-id")
    public ResponseEntity<IssuerTaxIdResource> getIssuerTaxIdByBranchId(@PathVariable UUID branchId) {
        var query = new GetIssuerTaxIdByBranchIdQuery(new BranchId(branchId));
        var taxId = branchQueryService.handle(query);
        if (taxId.isEmpty()) {
            return ResponseEntity.notFound().build();
        }

        return ResponseEntity.ok(new IssuerTaxIdResource(branchId, taxId.get()));
    }
}


