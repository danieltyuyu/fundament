package com.forkdevs.driveos.platform.fleet.domain.repositories;

import com.forkdevs.driveos.platform.fleet.domain.model.valueobjects.EmployeeId;
import com.forkdevs.driveos.platform.fleet.domain.model.aggregates.EmployeeRegistration;
import com.forkdevs.driveos.platform.fleet.domain.model.valueobjects.EmployeeRegistrationStatus;
import com.forkdevs.driveos.platform.shared.domain.model.valueobjects.BranchId;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface EmployeeRegistrationRepository {
    EmployeeRegistration save(EmployeeRegistration registration);
    Optional<EmployeeRegistration> findById(EmployeeId id);
    Optional<EmployeeRegistration> findByEmployeeId(UUID employeeId);
    List<EmployeeRegistration> findByBranchId(BranchId branchId);
    List<EmployeeRegistration> findByBranchIdAndStatus(BranchId branchId, EmployeeRegistrationStatus status);
    boolean existsByEmployeeIdAndBranchId(UUID employeeId, UUID branchId);
}
