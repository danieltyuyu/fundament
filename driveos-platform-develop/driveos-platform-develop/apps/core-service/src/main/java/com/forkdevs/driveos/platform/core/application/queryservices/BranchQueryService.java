package com.forkdevs.driveos.platform.core.application.queryservices;

import com.forkdevs.driveos.platform.core.domain.model.aggregates.Branch;
import com.forkdevs.driveos.platform.core.domain.model.queries.GetAllBranchesByWorkshopIdQuery;
import com.forkdevs.driveos.platform.core.domain.model.queries.GetBranchByIdQuery;
import com.forkdevs.driveos.platform.core.domain.model.queries.GetIssuerTaxIdByBranchIdQuery;

import java.util.List;
import java.util.Optional;

public interface BranchQueryService {
    Optional<Branch> handle(GetBranchByIdQuery query);
    List<Branch> handle(GetAllBranchesByWorkshopIdQuery query);
    Optional<String> handle(GetIssuerTaxIdByBranchIdQuery query);
}

