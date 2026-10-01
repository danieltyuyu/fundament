package com.forkdevs.driveos.platform.billing.infrastructure.persistence.jpa.repositories;

import com.forkdevs.driveos.platform.billing.infrastructure.persistence.jpa.entities.QuotePersistenceEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface QuotePersistenceRepository extends JpaRepository<QuotePersistenceEntity, UUID> {
    List<QuotePersistenceEntity> findAllByBranchId(com.forkdevs.driveos.platform.shared.domain.model.valueobjects.BranchId branchId);
}
