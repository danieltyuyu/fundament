package com.forkdevs.driveos.platform.fleet.application.queryservices;

import com.forkdevs.driveos.platform.fleet.domain.model.aggregates.CustomerRegistration;
import com.forkdevs.driveos.platform.fleet.domain.model.valueobjects.CustomerRegistrationStatus;
import com.forkdevs.driveos.platform.shared.application.result.Result;
import com.forkdevs.driveos.platform.fleet.domain.model.queries.GetCustomerRegistrationByCustomerIdQuery;
import com.forkdevs.driveos.platform.shared.domain.model.valueobjects.BranchId;

import java.util.List;
import java.util.UUID;

public interface CustomerRegistrationQueryService {

    Result<List<CustomerRegistration>, CustomerRegistrationQueryFailure> handle(BranchId branchId);

    Result<List<CustomerRegistration>, CustomerRegistrationQueryFailure> handle(BranchId branchId, CustomerRegistrationStatus status);

    Result<CustomerRegistration, CustomerRegistrationQueryFailure> handle(UUID registrationId);

    Result<CustomerRegistration, CustomerRegistrationQueryFailure> handle(GetCustomerRegistrationByCustomerIdQuery query);
}

