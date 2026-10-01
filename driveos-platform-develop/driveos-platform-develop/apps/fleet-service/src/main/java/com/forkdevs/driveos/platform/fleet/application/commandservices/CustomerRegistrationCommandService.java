package com.forkdevs.driveos.platform.fleet.application.commandservices;

import com.forkdevs.driveos.platform.fleet.domain.model.aggregates.CustomerRegistration;
import com.forkdevs.driveos.platform.fleet.domain.model.commands.CreateCustomerRegistrationCommand;
import com.forkdevs.driveos.platform.fleet.domain.model.commands.DeleteCustomerRegistrationCommand;
import com.forkdevs.driveos.platform.fleet.domain.model.commands.UpdateCustomerRegistrationCommand;
import com.forkdevs.driveos.platform.shared.application.result.Result;

import java.util.UUID;

public interface CustomerRegistrationCommandService {

    Result<CustomerRegistration, CustomerRegistrationCommandFailure> handle(CreateCustomerRegistrationCommand command);

    Result<CustomerRegistration, CustomerRegistrationCommandFailure> handle(UpdateCustomerRegistrationCommand command);

    Result<UUID, CustomerRegistrationCommandFailure> handle(DeleteCustomerRegistrationCommand command);
}

