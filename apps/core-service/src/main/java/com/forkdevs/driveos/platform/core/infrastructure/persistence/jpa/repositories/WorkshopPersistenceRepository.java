package com.forkdevs.driveos.platform.core.infrastructure.persistence.jpa.repositories;

import com.forkdevs.driveos.platform.core.infrastructure.persistence.jpa.entities.WorkshopPersistenceEntity;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface WorkshopPersistenceRepository extends JpaRepository<WorkshopPersistenceEntity, UUID> {
    List<WorkshopPersistenceEntity> findAllByOwnerId(UUID ownerId);
}
