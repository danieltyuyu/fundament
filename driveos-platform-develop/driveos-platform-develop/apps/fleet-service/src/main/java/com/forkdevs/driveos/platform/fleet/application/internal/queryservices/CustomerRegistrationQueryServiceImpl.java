package com.forkdevs.driveos.platform.fleet.application.internal.queryservices;

import com.forkdevs.driveos.platform.fleet.application.queryservices.CustomerRegistrationQueryFailure;
import com.forkdevs.driveos.platform.fleet.application.queryservices.CustomerRegistrationQueryService;
import com.forkdevs.driveos.platform.fleet.domain.model.aggregates.CustomerRegistration;
import com.forkdevs.driveos.platform.fleet.domain.model.valueobjects.CustomerRegistrationStatus;
import com.forkdevs.driveos.platform.fleet.domain.repositories.CustomerRegistrationRepository;
import com.forkdevs.driveos.platform.shared.application.result.Result;
import com.forkdevs.driveos.platform.fleet.domain.model.queries.GetCustomerRegistrationByCustomerIdQuery;
import com.forkdevs.driveos.platform.shared.domain.model.valueobjects.BranchId;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
public class CustomerRegistrationQueryServiceImpl implements CustomerRegistrationQueryService {

    private final CustomerRegistrationRepository repository;

    public CustomerRegistrationQueryServiceImpl(CustomerRegistrationRepository repository) {
        this.repository = repository;
    }

    @Override
    public Result<List<CustomerRegistration>, CustomerRegistrationQueryFailure> handle(BranchId branchId) {
        try {
            var list = repository.findByBranchIdAndStatus(branchId, CustomerRegistrationStatus.ACTIVE.value());
            return Result.success(list);
        } catch (IllegalArgumentException ex) {
            return Result.failure(CustomerRegistrationQueryFailure.INVALID_QUERY_PARAMS);
        }
    }

    @Override
    public Result<List<CustomerRegistration>, CustomerRegistrationQueryFailure> handle(BranchId branchId, CustomerRegistrationStatus status) {
        try {
            var list = repository.findByBranchIdAndStatus(branchId, status.value());
            return Result.success(list);
        } catch (IllegalArgumentException ex) {
            return Result.failure(CustomerRegistrationQueryFailure.INVALID_QUERY_PARAMS);
        }
    }

    @Override
    public Result<CustomerRegistration, CustomerRegistrationQueryFailure> handle(UUID registrationId) {
        try {
            var opt = repository.findById(registrationId);
            if (opt.isEmpty()) return Result.failure(CustomerRegistrationQueryFailure.REGISTRATION_NOT_FOUND);
            return Result.success(opt.get());
        } catch (IllegalArgumentException ex) {
            return Result.failure(CustomerRegistrationQueryFailure.INVALID_QUERY_PARAMS);
        }
    }

    @Override
    public Result<CustomerRegistration, CustomerRegistrationQueryFailure> handle(GetCustomerRegistrationByCustomerIdQuery query) {
        try {
            var opt = repository.findByCustomerId(query.customerId());
            if (opt.isEmpty()) return Result.failure(CustomerRegistrationQueryFailure.REGISTRATION_NOT_FOUND);
            return Result.success(opt.get());
        } catch (IllegalArgumentException ex) {
            return Result.failure(CustomerRegistrationQueryFailure.INVALID_QUERY_PARAMS);
        }
    }
}

