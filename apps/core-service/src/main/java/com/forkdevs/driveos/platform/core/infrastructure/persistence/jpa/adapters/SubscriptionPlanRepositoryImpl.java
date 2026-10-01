package com.forkdevs.driveos.platform.core.infrastructure.persistence.jpa.adapters;

import com.forkdevs.driveos.platform.core.domain.model.aggregates.SubscriptionPlan;
import com.forkdevs.driveos.platform.core.domain.model.valueobjects.SubscriptionPlanId;
import com.forkdevs.driveos.platform.core.domain.repositories.SubscriptionPlanRepository;
import com.forkdevs.driveos.platform.core.infrastructure.persistence.jpa.assemblers.SubscriptionPlanPersistenceAssembler;
import com.forkdevs.driveos.platform.core.infrastructure.persistence.jpa.entities.SubscriptionPlanPersistenceEntity;
import com.forkdevs.driveos.platform.core.infrastructure.persistence.jpa.repositories.SubscriptionPlanPersistenceRepository;

import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Repository
public class SubscriptionPlanRepositoryImpl implements SubscriptionPlanRepository {

    private final SubscriptionPlanPersistenceRepository subscriptionPlanPersistenceRepository;

    public SubscriptionPlanRepositoryImpl(SubscriptionPlanPersistenceRepository subscriptionPlanPersistenceRepository) {
        this.subscriptionPlanPersistenceRepository = subscriptionPlanPersistenceRepository;
    }

    @Override
    public SubscriptionPlan save(SubscriptionPlan subscriptionPlan) {
        SubscriptionPlanPersistenceEntity entity;
        if (subscriptionPlan.getId() != null) {
            entity = subscriptionPlanPersistenceRepository.findById(subscriptionPlan.getId().value()).orElse(new SubscriptionPlanPersistenceEntity());
        } else {
            entity = new SubscriptionPlanPersistenceEntity();
        }
        SubscriptionPlanPersistenceAssembler.toEntity(subscriptionPlan, entity);
        SubscriptionPlanPersistenceEntity savedEntity = subscriptionPlanPersistenceRepository.save(entity);
        return SubscriptionPlanPersistenceAssembler.toDomain(savedEntity);
    }

    @Override
    public Optional<SubscriptionPlan> findById(SubscriptionPlanId id) {
        return subscriptionPlanPersistenceRepository.findById(id.value()).map(SubscriptionPlanPersistenceAssembler::toDomain);
    }

    @Override
    public Optional<SubscriptionPlan> findByName(String name) {
        return subscriptionPlanPersistenceRepository.findByName(name).map(SubscriptionPlanPersistenceAssembler::toDomain);
    }

    @Override
    public List<SubscriptionPlan> findAll() {
        return subscriptionPlanPersistenceRepository.findAll().stream()
                .map(SubscriptionPlanPersistenceAssembler::toDomain)
                .collect(Collectors.toList());
    }
}
