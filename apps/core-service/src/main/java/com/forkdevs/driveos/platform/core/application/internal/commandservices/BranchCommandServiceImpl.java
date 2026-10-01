package com.forkdevs.driveos.platform.core.application.internal.commandservices;

import com.forkdevs.driveos.platform.core.application.commandservices.BranchCommandService;
import com.forkdevs.driveos.platform.core.domain.model.aggregates.Branch;
import com.forkdevs.driveos.platform.core.domain.model.commands.CreateBranchCommand;
import com.forkdevs.driveos.platform.core.domain.model.commands.UpdateBranchCommand;
import com.forkdevs.driveos.platform.core.domain.repositories.BranchRepository;
import com.forkdevs.driveos.platform.core.domain.repositories.WorkshopRepository;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class BranchCommandServiceImpl implements BranchCommandService {

    private final BranchRepository branchRepository;
    private final WorkshopRepository workshopRepository;

    public BranchCommandServiceImpl(BranchRepository branchRepository, WorkshopRepository workshopRepository) {
        this.branchRepository = branchRepository;
        this.workshopRepository = workshopRepository;
    }

    @Override
    public Optional<Branch> handle(CreateBranchCommand command) {
        if (!workshopRepository.existsById(command.workshopId())) {
            throw new IllegalArgumentException("core.error.workshop.notFound");
        }
        
        if (branchRepository.existsByCode(command.code())) {
            throw new IllegalArgumentException("core.error.branch.codeMustBeUnique");
        }

        var branch = new Branch(
                command.workshopId(),
                command.code(),
                command.name(),
                command.address(),
                command.phone()
        );

        var savedBranch = branchRepository.save(branch);
        return Optional.of(savedBranch);
    }

    @Override
    public Optional<Branch> handle(UpdateBranchCommand command) {
        var result = branchRepository.findById(command.id());
        if (result.isEmpty()) throw new IllegalArgumentException("core.error.branch.notFound");

        var branch = result.get();

        if (!branch.getCode().equals(command.code()) && branchRepository.existsByCode(command.code())) {
            throw new IllegalArgumentException("core.error.branch.codeMustBeUnique");
        }
        
        branch.update(
            command.code(),
            command.name(),
            command.address(),
            command.phone()
        );

        var savedBranch = branchRepository.save(branch);
        return Optional.of(savedBranch);
    }
}
