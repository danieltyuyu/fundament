package com.forkdevs.driveos.platform.fleet.application.commandservices;

import com.forkdevs.driveos.platform.fleet.domain.model.aggregates.Appointment;
import com.forkdevs.driveos.platform.fleet.domain.model.commands.CreateAppointmentCommand;
import com.forkdevs.driveos.platform.fleet.domain.model.commands.DeleteAppointmentCommand;
import com.forkdevs.driveos.platform.fleet.domain.model.commands.UpdateAppointmentCommand;
import com.forkdevs.driveos.platform.shared.application.result.Result;

import java.util.UUID;

public interface AppointmentCommandService {

    Result<Appointment, AppointmentCommandFailure> handle(CreateAppointmentCommand command);

    Result<Appointment, AppointmentCommandFailure> handle(UpdateAppointmentCommand command);

    Result<UUID, AppointmentCommandFailure> handle(DeleteAppointmentCommand command);
}