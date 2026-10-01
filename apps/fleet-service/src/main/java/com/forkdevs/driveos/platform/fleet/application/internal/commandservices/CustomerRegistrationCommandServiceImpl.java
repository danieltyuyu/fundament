package com.forkdevs.driveos.platform.fleet.application.internal.commandservices;

import com.forkdevs.driveos.platform.fleet.application.commandservices.CustomerRegistrationCommandFailure;
import com.forkdevs.driveos.platform.fleet.application.commandservices.CustomerRegistrationCommandService;
import com.forkdevs.driveos.platform.fleet.domain.model.aggregates.CustomerRegistration;
import com.forkdevs.driveos.platform.fleet.domain.model.commands.CreateCustomerRegistrationCommand;
import com.forkdevs.driveos.platform.fleet.domain.model.commands.DeleteCustomerRegistrationCommand;
import com.forkdevs.driveos.platform.fleet.domain.model.commands.UpdateCustomerRegistrationCommand;
import com.forkdevs.driveos.platform.fleet.domain.repositories.CustomerRegistrationRepository;
import com.forkdevs.driveos.platform.shared.application.result.Result;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
public class CustomerRegistrationCommandServiceImpl implements CustomerRegistrationCommandService {

    private final CustomerRegistrationRepository repository;

    public CustomerRegistrationCommandServiceImpl(CustomerRegistrationRepository repository) {
        this.repository = repository;
    }

    @Override
    @Transactional
    public Result<CustomerRegistration, CustomerRegistrationCommandFailure> handle(CreateCustomerRegistrationCommand command) {
        try {
            boolean exists = repository.existsByCustomerIdAndBranchId(command.customerId().value(), command.branchId().value());
            if (exists) {
                return Result.failure(CustomerRegistrationCommandFailure.REGISTRATION_ALREADY_EXISTS);
            }

            var registration = new CustomerRegistration(command.customerId().value(), command.branchId());
            var saved = repository.save(registration);
            return Result.success(saved);

        } catch (IllegalArgumentException ex) {
            return Result.failure(CustomerRegistrationCommandFailure.INVALID_REGISTRATION_DATA);
        }
    }

    @Override
    @Transactional
    public Result<CustomerRegistration, CustomerRegistrationCommandFailure> handle(UpdateCustomerRegistrationCommand command) {
        try {
            var opt = repository.findById(command.registrationId());
            if (opt.isEmpty()) {
                return Result.failure(CustomerRegistrationCommandFailure.REGISTRATION_NOT_FOUND);
            }

            var registration = opt.get();

            // Only status update supported for now (deactivate)
            if (command.status().value().equals("INACTIVE")) {
                registration.deactivate();
            }

            var updated = repository.save(registration);
            return Result.success(updated);

        } catch (IllegalArgumentException ex) {
            return Result.failure(CustomerRegistrationCommandFailure.INVALID_REGISTRATION_DATA);
        }
    }

    @Override
    @Transactional
    public Result<UUID, CustomerRegistrationCommandFailure> handle(DeleteCustomerRegistrationCommand command) {
        try {
            var opt = repository.findById(command.registrationId());
            if (opt.isEmpty()) {
                return Result.failure(CustomerRegistrationCommandFailure.REGISTRATION_NOT_FOUND);
            }

            var registration = opt.get();
            registration.deactivate();
            repository.save(registration);
            return Result.success(command.registrationId());

        } catch (IllegalArgumentException ex) {
            return Result.failure(CustomerRegistrationCommandFailure.INVALID_REGISTRATION_DATA);
        }
    }
}

