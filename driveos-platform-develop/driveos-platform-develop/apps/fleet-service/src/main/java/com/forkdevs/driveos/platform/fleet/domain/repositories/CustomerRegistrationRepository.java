package com.forkdevs.driveos.platform.fleet.domain.repositories;

import com.forkdevs.driveos.platform.fleet.domain.model.aggregates.CustomerRegistration;
import com.forkdevs.driveos.platform.shared.domain.model.valueobjects.BranchId;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface CustomerRegistrationRepository {
    CustomerRegistration save(CustomerRegistration registration);
    Optional<CustomerRegistration> findById(UUID id);
    Optional<CustomerRegistration> findByCustomerId(UUID customerId);
    Optional<CustomerRegistration> findByCustomerIdAndBranchId(UUID customerId, UUID branchId);
    List<CustomerRegistration> findByBranchIdAndStatus(BranchId branchId, String status);
    boolean existsByCustomerIdAndBranchId(UUID customerId, UUID branchId);
}

