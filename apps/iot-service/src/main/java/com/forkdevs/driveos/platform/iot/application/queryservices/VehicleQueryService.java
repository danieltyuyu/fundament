package com.forkdevs.driveos.platform.iot.application.queryservices;

import com.forkdevs.driveos.platform.iot.domain.model.aggregates.Vehicle;
import com.forkdevs.driveos.platform.iot.domain.model.queries.GetActiveVehiclesByCustomerIdQuery;
import com.forkdevs.driveos.platform.iot.domain.model.queries.GetVehiclesAvailableForLinkingQuery;
import com.forkdevs.driveos.platform.iot.domain.model.queries.GetVehicleByIdQuery;

import java.util.List;
import java.util.Optional;

/**
 * Service interface for handling Vehicle queries inside the iot context.
 */
public interface VehicleQueryService {

    /**
     * Handles retrieving all vehicles available for linking in a branch.
     * @param query the query containing the branch ID
     * @return the list of available vehicles
     */
    List<Vehicle> handle(GetVehiclesAvailableForLinkingQuery query);

    /**
     * Handles retrieving all active vehicles for a specific customer.
     * @param query the query containing the customer ID
     * @return the list of active vehicles
     */
    List<Vehicle> handle(GetActiveVehiclesByCustomerIdQuery query);

    /**
     * Handles retrieving a vehicle by its unique identifier.
     * @param query the query containing the vehicle ID
     * @return an Optional containing the vehicle if found, or empty
     */
    Optional<Vehicle> handle(GetVehicleByIdQuery query);
}
