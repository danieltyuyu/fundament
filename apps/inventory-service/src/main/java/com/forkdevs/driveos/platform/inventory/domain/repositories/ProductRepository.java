package com.forkdevs.driveos.platform.inventory.domain.repositories;

import com.forkdevs.driveos.platform.inventory.domain.model.aggregates.Product;
import com.forkdevs.driveos.platform.shared.domain.model.valueobjects.BranchId;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * Domain repository interface for the Product aggregate.
 * Defines the persistence contract without exposing infrastructure details.
 * @author Adiel Sanchez
 */
public interface ProductRepository {
    Product save(Product product);
    Optional<Product> findById(UUID id);

    /**
     * Retrieves all products belonging to the given branch.
     * @param branchId the branch identifier
     * @return list of products for that branch, may be empty
     */
    List<Product> findAllByBranchId(BranchId branchId);
    
    boolean existsById(UUID id);
    void deleteById(UUID id);
}
