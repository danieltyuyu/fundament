package com.forkdevs.driveos.platform.core.infrastructure.persistence.jpa.repositories;

import com.forkdevs.driveos.platform.core.infrastructure.persistence.jpa.entities.BranchPersistenceEntity;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface BranchPersistenceRepository extends JpaRepository<BranchPersistenceEntity, UUID> {
    List<BranchPersistenceEntity> findAllByWorkshopId(UUID workshopId);
    boolean existsByCode(String code);
}
