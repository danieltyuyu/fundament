package com.forkdevs.driveos.platform.iot.domain.repositories;

import com.forkdevs.driveos.platform.iot.domain.model.aggregates.Obd2Device;
import com.forkdevs.driveos.platform.iot.domain.model.valueobjects.Obd2DeviceId;
import com.forkdevs.driveos.platform.iot.domain.model.valueobjects.Obd2DeviceStatus;
import com.forkdevs.driveos.platform.shared.domain.model.valueobjects.BranchId;

import java.util.List;
import java.util.Optional;

/**
 * Domain repository interface for managing {@link Obd2Device} aggregates.
 */
public interface Obd2DeviceRepository {

    /**
     * Saves an Obd2Device aggregate.
     * @param obd2Device the aggregate to save
     * @return the saved aggregate
     */
    Obd2Device save(Obd2Device obd2Device);

    /**
     * Finds a device by its unique identifier.
     * @param id the unique identifier of the device
     * @return an Optional containing the aggregate if found
     */
    Optional<Obd2Device> findById(Obd2DeviceId id);

    /**
     * Finds a device by its MAC address.
     * @param macAddress the MAC address of the device
     * @return an Optional containing the aggregate if found
     */
    Optional<Obd2Device> findByMacAddress(String macAddress);

    /**
     * Checks if a device exists with the given MAC address.
     * @param macAddress the MAC address to check
     * @return true if a device exists, false otherwise
     */
    boolean existsByMacAddress(String macAddress);

    /**
     * Deletes (unregisters) a device from the repository by its ID.
     * @param id the unique identifier of the device
     */
    void delete(Obd2DeviceId id);

    /**
     * Finds all OBD2 devices registered in a specific branch.
     * @param branchId the unique identifier of the branch
     * @return the list of registered OBD2 devices
     */
    List<Obd2Device> findAllByBranchId(BranchId branchId);

    /**
     * Finds all OBD2 devices registered in a specific branch by their status.
     * @param branchId the unique identifier of the branch
     * @param status the device status
     * @return the list of matching OBD2 devices
     */
    List<Obd2Device> findAllByBranchIdAndStatus(BranchId branchId, Obd2DeviceStatus status);
}
