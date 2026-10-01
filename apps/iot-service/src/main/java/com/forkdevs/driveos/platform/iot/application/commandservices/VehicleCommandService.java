package com.forkdevs.driveos.platform.iot.application.commandservices;

import com.forkdevs.driveos.platform.iot.domain.model.aggregates.Vehicle;
import com.forkdevs.driveos.platform.iot.domain.model.aggregates.VehicleRegistration;
import com.forkdevs.driveos.platform.iot.domain.model.commands.DeleteVehicleCommand;
import com.forkdevs.driveos.platform.iot.domain.model.commands.RegisterVehicleCommand;
import com.forkdevs.driveos.platform.iot.domain.model.commands.UpdateVehicleCommand;
import com.forkdevs.driveos.platform.shared.application.result.Result;

/**
 * Service interface for handling Vehicle command operations.
 */
public interface VehicleCommandService {

    /**
     * Handles the registration of a new vehicle or transferring an existing one.
     * @param command the command containing registration/transfer details
     * @return a Result containing the active VehicleRegistration, or a VehicleCommandFailure
     */
    Result<VehicleRegistration, VehicleCommandFailure> handle(RegisterVehicleCommand command);

    /**
     * Handles updating an existing vehicle's details.
     * @param command the command containing update details
     * @return a Result containing the updated Vehicle aggregate, or a VehicleCommandFailure
     */
    Result<Vehicle, VehicleCommandFailure> handle(UpdateVehicleCommand command);

    /**
     * Handles deleting an existing vehicle.
     * @param command the command containing deletion details
     * @return a Result containing Void on success, or a VehicleCommandFailure
     */
    Result<Void, VehicleCommandFailure> handle(DeleteVehicleCommand command);
}
