package com.forkdevs.driveos.platform.core.application.queryservices;

import com.forkdevs.driveos.platform.core.domain.model.aggregates.Customer;
import com.forkdevs.driveos.platform.core.domain.model.queries.GetCustomerByIdQuery;
import com.forkdevs.driveos.platform.core.domain.model.queries.GetCustomerByUserIdQuery;

import java.util.Optional;

public interface CustomerQueryService {
    Optional<Customer> handle(GetCustomerByIdQuery query);
    Optional<Customer> handle(GetCustomerByUserIdQuery query);
}
