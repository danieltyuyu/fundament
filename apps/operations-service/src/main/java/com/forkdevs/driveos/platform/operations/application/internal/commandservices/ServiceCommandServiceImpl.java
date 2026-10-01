package com.forkdevs.driveos.platform.operations.application.internal.commandservices;

import com.forkdevs.driveos.platform.operations.domain.model.aggregates.Service;
import com.forkdevs.driveos.platform.operations.application.commandservices.ServiceCommandService;
import com.forkdevs.driveos.platform.operations.domain.model.commands.CreateServiceCommand;
import com.forkdevs.driveos.platform.operations.domain.model.commands.DeleteServiceCommand;
import com.forkdevs.driveos.platform.operations.domain.model.commands.UpdateServiceCommand;
import com.forkdevs.driveos.platform.operations.domain.repositories.ServiceRepository;

import java.util.Optional;

@org.springframework.stereotype.Service
public class ServiceCommandServiceImpl implements ServiceCommandService {

    private final ServiceRepository serviceRepository;

    public ServiceCommandServiceImpl(ServiceRepository serviceRepository) {
        this.serviceRepository = serviceRepository;
    }

    @Override
    public Optional<Service> handle(CreateServiceCommand command) {
        var service = new Service(
                command.branchId(),
                command.name(),
                command.price()
        );

        var savedService = serviceRepository.save(service);
        return Optional.of(savedService);
    }

    @Override
    public Optional<Service> handle(UpdateServiceCommand command) {
        var result = serviceRepository.findById(command.serviceId());
        if (result.isEmpty()) throw new IllegalArgumentException("operations.error.service.notFound");

        var service = result.get();

        service.update(
                command.name(),
                command.price()
        );

        var savedService = serviceRepository.save(service);
        return Optional.of(savedService);
    }

    @Override
    public void handle(DeleteServiceCommand command) {
        var existingService = serviceRepository.findById(command.serviceId());
        if (existingService.isEmpty()) {
            throw new IllegalArgumentException("operations.error.service.notFound");
        }

        serviceRepository.delete(existingService.get());
    }
}
