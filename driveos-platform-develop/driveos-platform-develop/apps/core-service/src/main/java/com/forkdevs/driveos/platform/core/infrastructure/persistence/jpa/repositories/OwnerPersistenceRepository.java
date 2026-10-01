package com.forkdevs.driveos.platform.core.infrastructure.persistence.jpa.repositories;

import com.forkdevs.driveos.platform.core.infrastructure.persistence.jpa.entities.OwnerPersistenceEntity;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface OwnerPersistenceRepository extends JpaRepository<OwnerPersistenceEntity, UUID> {
    Optional<OwnerPersistenceEntity> findByUserId(UUID userId);
    boolean existsByUserId(UUID userId);
    Optional<OwnerPersistenceEntity> findByDocumentNumber(String documentNumber);
}
