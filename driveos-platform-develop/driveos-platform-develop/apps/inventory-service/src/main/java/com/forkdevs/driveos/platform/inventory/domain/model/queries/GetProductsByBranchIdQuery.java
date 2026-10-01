package com.forkdevs.driveos.platform.inventory.domain.model.queries;

import com.forkdevs.driveos.platform.shared.domain.model.valueobjects.BranchId;

/**
 * Query representing the intention to retrieve all products belonging to a specific branch.
 * Used by the query service to filter the product catalog by branch (multi-tenant support).
 * @param branchId the unique identifier of the branch whose products are to be retrieved
 * @author Adiel Sanchez
 */
public record GetProductsByBranchIdQuery(BranchId branchId) {
}
