package com.forkdevs.driveos.platform.iot.domain.repositories;

import com.forkdevs.driveos.platform.iot.domain.model.aggregates.Vehicle;
import com.forkdevs.driveos.platform.shared.domain.model.valueobjects.BranchId;
import com.forkdevs.driveos.platform.shared.domain.model.valueobjects.VehicleId;

import java.util.List;
import java.util.Optional;

/**
 * Domain repository interface/port for managing Vehicle operations inside the iot context.
 */
public interface VehicleRepository {

    /**
     * Finds a vehicle by its unique identifier.
     * @param id the unique identifier of the vehicle
     * @return an Optional containing the Vehicle aggregate if found
     */
    Optional<Vehicle> findById(VehicleId id);

    /**
     * Finds all vehicles in a specific branch that are available for linking (unlinked).
     * @param branchId the unique identifier of the branch
     * @return the list of available vehicles
     */
    List<Vehicle> findAvailableForLinkingByBranchId(BranchId branchId);

    /**
     * Saves a Vehicle aggregate.
     * @param vehicle the aggregate to save
     * @return the saved aggregate
     */
    Vehicle save(Vehicle vehicle);

    /**
     * Finds a vehicle by its Vehicle Identification Number (VIN).
     * @param vin the VIN string
     * @return an Optional containing the Vehicle aggregate if found
     */
    Optional<Vehicle> findByVin(String vin);

    /**
     * Finds a vehicle by its plate number.
     * @param plateNumber the plate number string
     * @return an Optional containing the Vehicle aggregate if found
     */
    Optional<Vehicle> findByPlateNumber(String plateNumber);

    /**
     * Deletes (soft deletes) a vehicle from the repository.
     * @param id the unique identifier of the vehicle
     */
    void delete(VehicleId id);
}
