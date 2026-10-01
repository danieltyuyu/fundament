package com.forkdevs.driveos.platform.fleet.application.internal.commandservices;

import com.forkdevs.driveos.platform.fleet.application.commandservices.EmployeeRegistrationCommandFailure;
import com.forkdevs.driveos.platform.fleet.application.commandservices.EmployeeRegistrationCommandService;
import com.forkdevs.driveos.platform.fleet.domain.model.aggregates.EmployeeRegistration;
import com.forkdevs.driveos.platform.fleet.domain.model.commands.CreateEmployeeRegistrationCommand;
import com.forkdevs.driveos.platform.fleet.domain.model.commands.UpdateEmployeeRegistrationCommand;
import com.forkdevs.driveos.platform.fleet.domain.model.commands.DeleteEmployeeRegistrationCommand;
import com.forkdevs.driveos.platform.fleet.domain.repositories.EmployeeRegistrationRepository;
import com.forkdevs.driveos.platform.shared.application.result.Result;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class EmployeeRegistrationCommandServiceImpl implements EmployeeRegistrationCommandService {

    private final EmployeeRegistrationRepository repository;

    public EmployeeRegistrationCommandServiceImpl(EmployeeRegistrationRepository repository) {
        this.repository = repository;
    }

    @Override
    @Transactional
    public Result<EmployeeRegistration, EmployeeRegistrationCommandFailure> handle(
            CreateEmployeeRegistrationCommand command) {
        try {
            if (repository.existsByEmployeeIdAndBranchId(command.employeeId().value(), command.branchId().value()))
                return Result.failure(EmployeeRegistrationCommandFailure.REGISTRATION_ALREADY_EXISTS);

            var registration = new EmployeeRegistration(
                    command.employeeId().value(),
                    command.branchId(),
                    command.speciality(),
                    command.specialityName(),
                    command.salary());
            return Result.success(repository.save(registration));
        } catch (IllegalArgumentException ex) {
            return Result.failure(EmployeeRegistrationCommandFailure.INVALID_REGISTRATION_DATA);
        }
    }

    @Override
    @Transactional
    public Result<EmployeeRegistration, EmployeeRegistrationCommandFailure> handle(UpdateEmployeeRegistrationCommand command) {
        var registrationOptional = repository.findById(command.id());
        if (registrationOptional.isEmpty()) {
            return Result.failure(EmployeeRegistrationCommandFailure.REGISTRATION_NOT_FOUND);
        }

        var registration = registrationOptional.get();
        registration.update(command.speciality(), command.specialityName(), command.salary());
        
        var savedRegistration = repository.save(registration);
        return Result.success(savedRegistration);
    }

    @Override
    @Transactional
    public Result<EmployeeRegistration, EmployeeRegistrationCommandFailure> handle(DeleteEmployeeRegistrationCommand command) {
        var registrationOptional = repository.findById(command.id());
        if (registrationOptional.isEmpty()) {
            return Result.failure(EmployeeRegistrationCommandFailure.REGISTRATION_NOT_FOUND);
        }

        var registration = registrationOptional.get();
        registration.deactivate();
        
        var savedRegistration = repository.save(registration);
        return Result.success(savedRegistration);
    }
}