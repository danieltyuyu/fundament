package com.forkdevs.driveos.platform.operations.application.queryservices;

import com.forkdevs.driveos.platform.operations.domain.model.aggregates.Service;
import com.forkdevs.driveos.platform.operations.domain.model.queries.GetAllServicesByBranchIdQuery;
import com.forkdevs.driveos.platform.operations.domain.model.queries.GetServiceByIdQuery;

import java.util.List;
import java.util.Optional;

public interface ServiceQueryService {
    Optional<Service> handle(GetServiceByIdQuery query);
    List<Service> handle(GetAllServicesByBranchIdQuery query);
}
