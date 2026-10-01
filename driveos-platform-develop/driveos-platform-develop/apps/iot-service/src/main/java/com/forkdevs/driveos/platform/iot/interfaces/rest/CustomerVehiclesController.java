package com.forkdevs.driveos.platform.iot.interfaces.rest;

import com.forkdevs.driveos.platform.iot.application.queryservices.VehicleQueryService;
import com.forkdevs.driveos.platform.iot.domain.model.queries.GetActiveVehiclesByCustomerIdQuery;
import com.forkdevs.driveos.platform.shared.domain.model.valueobjects.CustomerId;
import com.forkdevs.driveos.platform.iot.interfaces.rest.resources.VehicleResource;
import com.forkdevs.driveos.platform.iot.interfaces.rest.transform.VehicleResourceFromAggregateAssembler;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

/**
 * REST controller for exposing customer-specific vehicles operations.
 * Maps to /api/v1/customers prefix to maintain domain boundaries within iot.
 */
@RestController
@RequestMapping(value = "/api/v1/customers", produces = "application/json")
@Tag(name = "Customers", description = "Endpoints for managing client integrations with other contexts")
public class CustomerVehiclesController {

    private final VehicleQueryService vehicleQueryService;

    public CustomerVehiclesController(VehicleQueryService vehicleQueryService) {
        this.vehicleQueryService = vehicleQueryService;
    }

    /**
     * Gets all vehicles associated with an active registration for a specific customer.
     * @param customerId the customer identifier UUID
     * @return the list of active vehicles for the customer
     */
    @GetMapping("/{customerId}/vehicles")
    @Operation(summary = "Get active vehicles for customer", description = "Retrieves all vehicles currently associated with an active registration for the customer")
    public ResponseEntity<List<VehicleResource>> getActiveVehiclesByCustomerId(@PathVariable UUID customerId) {
        var query = new GetActiveVehiclesByCustomerIdQuery(new CustomerId(customerId));
        var list = vehicleQueryService.handle(query);
        var resources = list.stream()
                .map(VehicleResourceFromAggregateAssembler::toResourceFromAggregate)
                .toList();
        return ResponseEntity.ok(resources);
    }
}
