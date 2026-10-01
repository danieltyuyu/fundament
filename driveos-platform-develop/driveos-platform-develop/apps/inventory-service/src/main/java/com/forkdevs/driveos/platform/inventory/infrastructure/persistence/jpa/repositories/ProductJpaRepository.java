package com.forkdevs.driveos.platform.inventory.infrastructure.persistence.jpa.repositories;

import com.forkdevs.driveos.platform.inventory.infrastructure.persistence.jpa.entities.ProductJpaEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

/**
 * Spring Data JPA repository for the ProductJpaEntity.
 * Provides derived query methods that translate to SQL automatically.
 * @author Adiel Sanchez
 */
@Repository
public interface ProductJpaRepository extends JpaRepository<ProductJpaEntity, UUID> {

    /**
     * Finds all products whose branchId column matches the given string.
     * Note: branchId is stored as a String (UUID.toString()) in the database.
     * @param branchId the branch UUID
     * @return list of matching product entities
     */
    List<ProductJpaEntity> findAllByBranchId(UUID branchId);
}
