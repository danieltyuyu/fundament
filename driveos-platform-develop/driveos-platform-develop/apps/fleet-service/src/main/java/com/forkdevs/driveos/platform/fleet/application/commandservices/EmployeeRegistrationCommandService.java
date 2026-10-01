package com.forkdevs.driveos.platform.fleet.application.commandservices;

import com.forkdevs.driveos.platform.fleet.domain.model.aggregates.EmployeeRegistration;
import com.forkdevs.driveos.platform.fleet.domain.model.commands.CreateEmployeeRegistrationCommand;
import com.forkdevs.driveos.platform.fleet.domain.model.commands.UpdateEmployeeRegistrationCommand;
import com.forkdevs.driveos.platform.fleet.domain.model.commands.DeleteEmployeeRegistrationCommand;
import com.forkdevs.driveos.platform.shared.application.result.Result;

public interface EmployeeRegistrationCommandService {
    Result<EmployeeRegistration, EmployeeRegistrationCommandFailure> handle(CreateEmployeeRegistrationCommand command);
    Result<EmployeeRegistration, EmployeeRegistrationCommandFailure> handle(UpdateEmployeeRegistrationCommand command);
    Result<EmployeeRegistration, EmployeeRegistrationCommandFailure> handle(DeleteEmployeeRegistrationCommand command);
}
