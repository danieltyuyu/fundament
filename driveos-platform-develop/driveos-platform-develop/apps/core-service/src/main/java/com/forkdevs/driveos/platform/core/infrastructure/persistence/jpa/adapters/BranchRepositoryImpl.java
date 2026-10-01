package com.forkdevs.driveos.platform.core.infrastructure.persistence.jpa.adapters;

import com.forkdevs.driveos.platform.core.domain.model.aggregates.Branch;
import com.forkdevs.driveos.platform.shared.domain.model.valueobjects.BranchId;
import com.forkdevs.driveos.platform.core.domain.model.valueobjects.WorkshopId;
import com.forkdevs.driveos.platform.core.domain.repositories.BranchRepository;
import com.forkdevs.driveos.platform.core.infrastructure.persistence.jpa.assemblers.BranchPersistenceAssembler;
import com.forkdevs.driveos.platform.core.infrastructure.persistence.jpa.entities.BranchPersistenceEntity;
import com.forkdevs.driveos.platform.core.infrastructure.persistence.jpa.repositories.BranchPersistenceRepository;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import org.springframework.stereotype.Repository;

@Repository
public class BranchRepositoryImpl implements BranchRepository {

    private final BranchPersistenceRepository branchPersistenceRepository;

    public BranchRepositoryImpl(BranchPersistenceRepository branchPersistenceRepository) {
        this.branchPersistenceRepository = branchPersistenceRepository;
    }

    @Override
    public Branch save(Branch branch) {
        BranchPersistenceEntity entity;
        if (branch.getId() != null) {
            entity = branchPersistenceRepository.findById(branch.getId().value()).orElse(new BranchPersistenceEntity());
        } else {
            entity = new BranchPersistenceEntity();
        }
        BranchPersistenceAssembler.toEntity(branch, entity);
        BranchPersistenceEntity savedEntity = branchPersistenceRepository.save(entity);
        return BranchPersistenceAssembler.toDomain(savedEntity);
    }

    @Override
    public Optional<Branch> findById(BranchId id) {
        return branchPersistenceRepository.findById(id.value()).map(BranchPersistenceAssembler::toDomain);
    }

    @Override
    public List<Branch> findAllByWorkshopId(WorkshopId workshopId) {
        return branchPersistenceRepository.findAllByWorkshopId(workshopId.value()).stream()
                .map(BranchPersistenceAssembler::toDomain)
                .collect(Collectors.toList());
    }

    @Override
    public boolean existsById(BranchId id) {
        return branchPersistenceRepository.existsById(id.value());
    }

    @Override
    public boolean existsByCode(String code) {
        return branchPersistenceRepository.existsByCode(code);
    }
}

