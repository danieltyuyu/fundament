package com.forkdevs.driveos.platform.iot.domain.repositories;

import com.forkdevs.driveos.platform.iot.domain.model.aggregates.Obd2DeviceRegistration;
import com.forkdevs.driveos.platform.iot.domain.model.valueobjects.Obd2DeviceId;
import com.forkdevs.driveos.platform.iot.domain.model.valueobjects.Obd2DeviceRegistrationId;
import com.forkdevs.driveos.platform.iot.domain.model.valueobjects.Obd2RegistrationStatus;
import com.forkdevs.driveos.platform.shared.domain.model.valueobjects.BranchId;
import com.forkdevs.driveos.platform.shared.domain.model.valueobjects.VehicleId;

import java.util.List;
import java.util.Optional;

/**
 * Domain repository interface for managing {@link Obd2DeviceRegistration} aggregates.
 */
public interface Obd2DeviceRegistrationRepository {

    /**
     * Saves an Obd2DeviceRegistration aggregate.
     * @param registration the aggregate to save
     * @return the saved aggregate
     */
    Obd2DeviceRegistration save(Obd2DeviceRegistration registration);

    /**
     * Finds a registration by its unique identifier.
     * @param id the unique identifier of the registration
     * @return an Optional containing the aggregate if found
     */
    Optional<Obd2DeviceRegistration> findById(Obd2DeviceRegistrationId id);

    /**
     * Finds the active registration for a specific OBD2 device.
     * @param obd2DeviceId the unique identifier of the OBD2 device
     * @return an Optional containing the active registration if found
     */
    Optional<Obd2DeviceRegistration> findActiveByObd2DeviceId(Obd2DeviceId obd2DeviceId);

    /**
     * Finds the active registration for a specific vehicle.
     * @param vehicleId the unique identifier of the vehicle
     * @return an Optional containing the active registration if found
     */
    Optional<Obd2DeviceRegistration> findActiveByVehicleId(VehicleId vehicleId);

    /**
     * Finds all registrations for a specific branch and status.
     * @param branchId the branch identifier
     * @param status the registration status
     * @return the list of registrations matching branch and status
     */
    List<Obd2DeviceRegistration> findAllByBranchIdAndStatus(BranchId branchId, Obd2RegistrationStatus status);
}
