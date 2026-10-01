package com.forkdevs.driveos.platform.core.infrastructure.persistence.jpa.adapters;

import com.forkdevs.driveos.platform.core.domain.model.aggregates.BranchSubscription;
import com.forkdevs.driveos.platform.shared.domain.model.valueobjects.BranchId;
import com.forkdevs.driveos.platform.core.domain.model.valueobjects.BranchSubscriptionId;
import com.forkdevs.driveos.platform.core.domain.model.valueobjects.SubscriptionStatus;
import com.forkdevs.driveos.platform.core.domain.repositories.BranchSubscriptionRepository;
import com.forkdevs.driveos.platform.core.infrastructure.persistence.jpa.assemblers.BranchSubscriptionPersistenceAssembler;
import com.forkdevs.driveos.platform.core.infrastructure.persistence.jpa.entities.BranchSubscriptionPersistenceEntity;
import com.forkdevs.driveos.platform.core.infrastructure.persistence.jpa.repositories.BranchSubscriptionPersistenceRepository;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import org.springframework.stereotype.Repository;

@Repository
public class BranchSubscriptionRepositoryImpl implements BranchSubscriptionRepository {

    private final BranchSubscriptionPersistenceRepository branchSubscriptionPersistenceRepository;

    public BranchSubscriptionRepositoryImpl(BranchSubscriptionPersistenceRepository branchSubscriptionPersistenceRepository) {
        this.branchSubscriptionPersistenceRepository = branchSubscriptionPersistenceRepository;
    }

    @Override
    public BranchSubscription save(BranchSubscription branchSubscription) {
        BranchSubscriptionPersistenceEntity entity;
        if (branchSubscription.getId() != null) {
            entity = branchSubscriptionPersistenceRepository.findById(branchSubscription.getId().value()).orElse(new BranchSubscriptionPersistenceEntity());
        } else {
            entity = new BranchSubscriptionPersistenceEntity();
        }
        BranchSubscriptionPersistenceAssembler.toEntity(branchSubscription, entity);
        BranchSubscriptionPersistenceEntity savedEntity = branchSubscriptionPersistenceRepository.save(entity);
        return BranchSubscriptionPersistenceAssembler.toDomain(savedEntity);
    }

    @Override
    public Optional<BranchSubscription> findById(BranchSubscriptionId id) {
        return branchSubscriptionPersistenceRepository.findById(id.value()).map(BranchSubscriptionPersistenceAssembler::toDomain);
    }

    @Override
    public List<BranchSubscription> findAllByBranchId(BranchId branchId) {
        return branchSubscriptionPersistenceRepository.findAllByBranchId(branchId.value()).stream()
                .map(BranchSubscriptionPersistenceAssembler::toDomain)
                .collect(Collectors.toList());
    }

    @Override
    public Optional<BranchSubscription> findActiveByBranchId(BranchId branchId) {
        return branchSubscriptionPersistenceRepository.findByBranchIdAndStatus(branchId.value(), SubscriptionStatus.ACTIVE)
                .map(BranchSubscriptionPersistenceAssembler::toDomain);
    }
}

