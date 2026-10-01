package com.forkdevs.driveos.platform.iot.domain.repositories;

import com.forkdevs.driveos.platform.iot.domain.model.aggregates.VehicleRegistration;
import com.forkdevs.driveos.platform.shared.domain.model.valueobjects.VehicleId;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * Domain repository interface/port for managing VehicleRegistration operations inside the iot context.
 */
public interface VehicleRegistrationRepository {

    /**
     * Saves a VehicleRegistration aggregate.
     * @param registration the aggregate to save
     * @return the saved aggregate
     */
    VehicleRegistration save(VehicleRegistration registration);

    /**
     * Finds the active registration for a specific vehicle.
     * @param vehicleId the unique identifier of the vehicle
     * @return an Optional containing the active registration if found
     */
    Optional<VehicleRegistration> findActiveByVehicleId(VehicleId vehicleId);

    /**
     * Finds all active vehicle registrations for a specific driver/user.
     * @param userId the unique identifier of the driver/user
     * @return the list of active registrations
     */
    List<VehicleRegistration> findAllActiveByUserId(UUID userId);
}
