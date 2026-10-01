package com.forkdevs.driveos.platform.core.infrastructure.persistence.jpa.adapters;

import com.forkdevs.driveos.platform.core.domain.model.aggregates.Owner;
import com.forkdevs.driveos.platform.core.domain.model.valueobjects.OwnerId;
import com.forkdevs.driveos.platform.core.domain.model.valueobjects.UserId;
import com.forkdevs.driveos.platform.core.domain.repositories.OwnerRepository;
import com.forkdevs.driveos.platform.core.infrastructure.persistence.jpa.assemblers.OwnerPersistenceAssembler;
import com.forkdevs.driveos.platform.core.infrastructure.persistence.jpa.entities.OwnerPersistenceEntity;
import com.forkdevs.driveos.platform.core.infrastructure.persistence.jpa.repositories.OwnerPersistenceRepository;

import org.springframework.stereotype.Repository;
import java.util.Optional;

@Repository
public class OwnerRepositoryImpl implements OwnerRepository {

    private final OwnerPersistenceRepository ownerPersistenceRepository;

    public OwnerRepositoryImpl(OwnerPersistenceRepository ownerPersistenceRepository) {
        this.ownerPersistenceRepository = ownerPersistenceRepository;
    }

    @Override
    public Owner save(Owner owner) {
        OwnerPersistenceEntity entity;
        if (owner.getId() != null) {
            entity = ownerPersistenceRepository.findById(owner.getId().value()).orElse(new OwnerPersistenceEntity());
        } else {
            entity = new OwnerPersistenceEntity();
        }
        
        OwnerPersistenceAssembler.toEntity(owner, entity);
        OwnerPersistenceEntity savedEntity = ownerPersistenceRepository.save(entity);
        return OwnerPersistenceAssembler.toDomain(savedEntity);
    }

    @Override
    public Optional<Owner> findById(OwnerId id) {
        return ownerPersistenceRepository.findById(id.value()).map(OwnerPersistenceAssembler::toDomain);
    }

    @Override
    public Optional<Owner> findByUserId(UserId userId) {
        return ownerPersistenceRepository.findByUserId(userId.value()).map(OwnerPersistenceAssembler::toDomain);
    }

    @Override
    public boolean existsByUserId(UserId userId) {
        return ownerPersistenceRepository.existsByUserId(userId.value());
    }

    @Override
    public Optional<Owner> findByDocumentNumber(String documentNumber) {
        return ownerPersistenceRepository.findByDocumentNumber(documentNumber).map(OwnerPersistenceAssembler::toDomain);
    }

    @Override
    public boolean existsById(OwnerId id) {
        return ownerPersistenceRepository.existsById(id.value());
    }

    @Override
    public void delete(Owner owner) {
        if (owner.getId() != null) {
            ownerPersistenceRepository.findById(owner.getId().value()).ifPresent(ownerPersistenceRepository::delete);
        }
    }
}
