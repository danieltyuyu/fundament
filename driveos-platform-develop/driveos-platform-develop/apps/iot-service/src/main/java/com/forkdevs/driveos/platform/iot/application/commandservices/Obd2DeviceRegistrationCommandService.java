package com.forkdevs.driveos.platform.iot.application.commandservices;

import com.forkdevs.driveos.platform.iot.domain.model.aggregates.Obd2DeviceRegistration;
import com.forkdevs.driveos.platform.iot.domain.model.commands.DeactivateObd2DeviceRegistrationCommand;
import com.forkdevs.driveos.platform.iot.domain.model.commands.LinkObd2DeviceToVehicleCommand;
import com.forkdevs.driveos.platform.shared.application.result.Result;

/**
 * Service interface for handling OBD2 device registration commands.
 */
public interface Obd2DeviceRegistrationCommandService {

    /**
     * Handles linking an OBD2 device to a vehicle.
     * @param command the command containing linking details
     * @return a Result containing the successfully linked registration, or an Obd2DeviceRegistrationCommandFailure
     */
    Result<Obd2DeviceRegistration, Obd2DeviceRegistrationCommandFailure> handle(LinkObd2DeviceToVehicleCommand command);

    /**
     * Handles deactivating an OBD2 device registration (unlinking it from a vehicle).
     * @param command the command containing deactivation details
     * @return a Result containing the deactivated registration, or an Obd2DeviceRegistrationCommandFailure
     */
    Result<Obd2DeviceRegistration, Obd2DeviceRegistrationCommandFailure> handle(DeactivateObd2DeviceRegistrationCommand command);
}

