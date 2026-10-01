package com.forkdevs.driveos.platform.core.infrastructure.persistence.jpa.repositories;

import com.forkdevs.driveos.platform.core.infrastructure.persistence.jpa.entities.CustomerPersistenceEntity;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface CustomerPersistenceRepository extends JpaRepository<CustomerPersistenceEntity, UUID> {
    Optional<CustomerPersistenceEntity> findByUserId(UUID userId);
    boolean existsByUserId(UUID userId);
    Optional<CustomerPersistenceEntity> findByDocumentNumber(String documentNumber);
}
