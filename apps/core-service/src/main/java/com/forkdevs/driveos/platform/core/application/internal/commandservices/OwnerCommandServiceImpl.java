package com.forkdevs.driveos.platform.core.application.internal.commandservices;

import com.forkdevs.driveos.platform.core.application.commandservices.OwnerCommandService;
import com.forkdevs.driveos.platform.core.domain.model.aggregates.Owner;
import com.forkdevs.driveos.platform.core.domain.model.commands.CreateOwnerCommand;
import com.forkdevs.driveos.platform.core.domain.model.commands.DeleteOwnerCommand;
import com.forkdevs.driveos.platform.core.domain.model.commands.UpdateOwnerCommand;
import com.forkdevs.driveos.platform.core.domain.repositories.OwnerRepository;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class OwnerCommandServiceImpl implements OwnerCommandService {

    private final OwnerRepository ownerRepository;

    public OwnerCommandServiceImpl(OwnerRepository ownerRepository) {
        this.ownerRepository = ownerRepository;
    }

    @Override
    public Optional<Owner> handle(CreateOwnerCommand command) {
        if (ownerRepository.existsByUserId(command.userId())) {
            throw new IllegalArgumentException("core.error.owner.profileAlreadyExists");
        }

        var owner = new Owner(
                command.userId(),
                command.name(),
                command.document(),
                command.phone()
        );

        var savedOwner = ownerRepository.save(owner);
        return Optional.of(savedOwner);
    }

    @Override
    public Optional<Owner> handle(UpdateOwnerCommand command) {
        var result = ownerRepository.findById(command.ownerId());
        if (result.isEmpty()) throw new IllegalArgumentException("core.error.owner.notFound");

        var owner = result.get();
        
        owner.update(
            command.name(),
            command.document(),
            command.phone()
        );

        var savedOwner = ownerRepository.save(owner);
        return Optional.of(savedOwner);
    }

    @Override
    public void handle(DeleteOwnerCommand command) {
        var existingOwner = ownerRepository.findById(command.ownerId());
        if (existingOwner.isEmpty()) {
            throw new IllegalArgumentException("core.error.owner.notFound");
        }
        
        ownerRepository.delete(existingOwner.get());
    }
}
