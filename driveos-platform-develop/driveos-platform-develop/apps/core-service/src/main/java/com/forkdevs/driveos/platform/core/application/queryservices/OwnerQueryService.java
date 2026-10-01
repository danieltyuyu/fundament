package com.forkdevs.driveos.platform.core.application.queryservices;

import com.forkdevs.driveos.platform.core.domain.model.aggregates.Owner;
import com.forkdevs.driveos.platform.core.domain.model.queries.GetOwnerByIdQuery;

import com.forkdevs.driveos.platform.core.domain.model.queries.GetOwnerByUserIdQuery;

import java.util.Optional;

public interface OwnerQueryService {
    Optional<Owner> handle(GetOwnerByIdQuery query);
    Optional<Owner> handle(GetOwnerByUserIdQuery query);
}
