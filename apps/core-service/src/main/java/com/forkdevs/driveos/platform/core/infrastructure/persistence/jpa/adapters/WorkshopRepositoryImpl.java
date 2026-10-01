package com.forkdevs.driveos.platform.core.infrastructure.persistence.jpa.adapters;

import com.forkdevs.driveos.platform.core.domain.model.aggregates.Workshop;
import com.forkdevs.driveos.platform.core.domain.model.valueobjects.OwnerId;
import com.forkdevs.driveos.platform.core.domain.model.valueobjects.WorkshopId;
import com.forkdevs.driveos.platform.core.domain.repositories.WorkshopRepository;
import com.forkdevs.driveos.platform.core.infrastructure.persistence.jpa.assemblers.WorkshopPersistenceAssembler;
import com.forkdevs.driveos.platform.core.infrastructure.persistence.jpa.entities.WorkshopPersistenceEntity;
import com.forkdevs.driveos.platform.core.infrastructure.persistence.jpa.repositories.WorkshopPersistenceRepository;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import org.springframework.stereotype.Repository;

@Repository
public class WorkshopRepositoryImpl implements WorkshopRepository {

    private final WorkshopPersistenceRepository workshopPersistenceRepository;

    public WorkshopRepositoryImpl(WorkshopPersistenceRepository workshopPersistenceRepository) {
        this.workshopPersistenceRepository = workshopPersistenceRepository;
    }

    @Override
    public Workshop save(Workshop workshop) {
        WorkshopPersistenceEntity entity;
        if (workshop.getId() != null) {
            entity = workshopPersistenceRepository.findById(workshop.getId().value()).orElse(new WorkshopPersistenceEntity());
        } else {
            entity = new WorkshopPersistenceEntity();
        }
        WorkshopPersistenceAssembler.toEntity(workshop, entity);
        WorkshopPersistenceEntity savedEntity = workshopPersistenceRepository.save(entity);
        return WorkshopPersistenceAssembler.toDomain(savedEntity);
    }

    @Override
    public Optional<Workshop> findById(WorkshopId id) {
        return workshopPersistenceRepository.findById(id.value()).map(WorkshopPersistenceAssembler::toDomain);
    }

    @Override
    public List<Workshop> findAllByOwnerId(OwnerId ownerId) {
        return workshopPersistenceRepository.findAllByOwnerId(ownerId.value()).stream()
                .map(WorkshopPersistenceAssembler::toDomain)
                .collect(Collectors.toList());
    }

    @Override
    public boolean existsById(WorkshopId id) {
        return workshopPersistenceRepository.existsById(id.value());
    }
}
