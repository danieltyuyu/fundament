package com.forkdevs.driveos.platform.inventory.application.queryservices;

import com.forkdevs.driveos.platform.inventory.domain.model.aggregates.Product;
import com.forkdevs.driveos.platform.inventory.domain.model.queries.GetProductByIdQuery;
import com.forkdevs.driveos.platform.inventory.domain.model.queries.GetProductsByBranchIdQuery;

import java.util.List;
import java.util.Optional;

/**
 * Application service contract providing read access to Products.
 * Exposes query operations used by the interface layer to retrieve data
 * without exposing persistence details. Follows the CQRS pattern,
 * keeping reads completely separated from command handling.
 * @author Adiel Sanchez
 */
public interface ProductQueryService {

    /**
     * Handles the retrieval of all Products associated with a specific Branch ID.
     * @param query The query object containing the Branch ID for which to retrieve products.
     * @return A list of Products associated with the specified Branch ID.
     */
    List<Product> handle(GetProductsByBranchIdQuery query);
    Optional<Product> handle(GetProductByIdQuery query);
}
