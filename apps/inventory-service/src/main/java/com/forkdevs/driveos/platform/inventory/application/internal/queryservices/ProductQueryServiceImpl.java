package com.forkdevs.driveos.platform.inventory.application.internal.queryservices;

import com.forkdevs.driveos.platform.inventory.application.queryservices.ProductQueryService;
import com.forkdevs.driveos.platform.inventory.domain.model.aggregates.Product;
import com.forkdevs.driveos.platform.inventory.domain.model.queries.GetProductByIdQuery;
import com.forkdevs.driveos.platform.inventory.domain.model.queries.GetProductsByBranchIdQuery;
import com.forkdevs.driveos.platform.inventory.domain.repositories.ProductRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

/**
 * Internal application service implementing {@link ProductQueryService}.
 * Provides read-only transactional access to product aggregates.
 * Follows the CQRS pattern: this class only handles queries, never commands.
 * @author Adiel Sanchez
 */
@Service
@Transactional(readOnly = true)
public class ProductQueryServiceImpl implements ProductQueryService {

    private final ProductRepository productRepository;

    /**
     * Constructor for ProductQueryServiceImpl.
     * @param productRepository Repository for accessing product data from the database.
     */
    public ProductQueryServiceImpl(ProductRepository productRepository) {
        this.productRepository = productRepository;
    }

    /**
     * Handles the GetProductsByBranchIdQuery by retrieving all products for the given branch.
     * @param query The query containing the BranchId to filter by.
     * @return A list of Product aggregates belonging to the specified branch.
     * @throws IllegalArgumentException if the branchId in the query is null.
     */
    @Override
    public List<Product> handle(GetProductsByBranchIdQuery query) {
        if (query.branchId() == null) {
            throw new IllegalArgumentException("inventory.error.query.branchId.required");
        }
        return productRepository.findAllByBranchId(query.branchId());
    }

    @Override
    public Optional<Product> handle(GetProductByIdQuery query) {
        return productRepository.findById(query.productId());
    }
}
