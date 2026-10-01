package com.forkdevs.driveos.platform.core.application.commandservices;

import com.forkdevs.driveos.platform.core.domain.model.aggregates.Employee;
import com.forkdevs.driveos.platform.core.domain.model.commands.CreateEmployeeCommand;
import com.forkdevs.driveos.platform.core.domain.model.commands.DeleteEmployeeCommand;
import com.forkdevs.driveos.platform.core.domain.model.commands.UpdateEmployeeCommand;

import java.util.Optional;

public interface EmployeeCommandService {
    Optional<Employee> handle(CreateEmployeeCommand command);
    Optional<Employee> handle(UpdateEmployeeCommand command);
    void handle(DeleteEmployeeCommand command);
}
