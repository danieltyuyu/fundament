package com.forkdevs.driveos.platform.core.application.commandservices;

import com.forkdevs.driveos.platform.core.domain.model.aggregates.Customer;
import com.forkdevs.driveos.platform.core.domain.model.commands.CreateCustomerCommand;
import com.forkdevs.driveos.platform.core.domain.model.commands.DeleteCustomerCommand;
import com.forkdevs.driveos.platform.core.domain.model.commands.UpdateCustomerCommand;

import java.util.Optional;

public interface CustomerCommandService {
    Optional<Customer> handle(CreateCustomerCommand command);
    Optional<Customer> handle(UpdateCustomerCommand command);
    void handle(DeleteCustomerCommand command);
}
