package com.forkdevs.driveos.platform.core.application.internal.commandservices;

import com.forkdevs.driveos.platform.core.application.commandservices.EmployeeCommandService;
import com.forkdevs.driveos.platform.core.domain.model.aggregates.Employee;
import com.forkdevs.driveos.platform.core.domain.model.commands.CreateEmployeeCommand;
import com.forkdevs.driveos.platform.core.domain.model.commands.DeleteEmployeeCommand;
import com.forkdevs.driveos.platform.core.domain.model.commands.UpdateEmployeeCommand;
import com.forkdevs.driveos.platform.core.domain.repositories.EmployeeRepository;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class EmployeeCommandServiceImpl implements EmployeeCommandService {

    private final EmployeeRepository employeeRepository;

    public EmployeeCommandServiceImpl(EmployeeRepository employeeRepository) {
        this.employeeRepository = employeeRepository;
    }

    @Override
    public Optional<Employee> handle(CreateEmployeeCommand command) {
        if (employeeRepository.existsByUserId(command.userId())) {
            throw new IllegalArgumentException("core.error.employee.profileAlreadyExists");
        }

        var employee = new Employee(
                command.userId(),
                command.name(),
                command.document(),
                command.phone()
        );

        var savedEmployee = employeeRepository.save(employee);
        return Optional.of(savedEmployee);
    }

    @Override
    public Optional<Employee> handle(UpdateEmployeeCommand command) {
        var result = employeeRepository.findById(command.employeeId());
        if (result.isEmpty()) throw new IllegalArgumentException("core.error.employee.notFound");

        var employee = result.get();
        
        employee.update(
            command.name(),
            command.document(),
            command.phone()
        );

        var savedEmployee = employeeRepository.save(employee);
        return Optional.of(savedEmployee);
    }

    @Override
    public void handle(DeleteEmployeeCommand command) {
        var existingEmployee = employeeRepository.findById(command.employeeId());
        if (existingEmployee.isEmpty()) {
            throw new IllegalArgumentException("core.error.employee.notFound");
        }
        
        employeeRepository.delete(existingEmployee.get());
    }
}
