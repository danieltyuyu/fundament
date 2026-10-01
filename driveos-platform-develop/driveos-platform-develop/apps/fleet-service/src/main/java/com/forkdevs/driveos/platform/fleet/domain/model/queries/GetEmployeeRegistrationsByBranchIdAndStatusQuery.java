package com.forkdevs.driveos.platform.fleet.domain.model.queries;

import com.forkdevs.driveos.platform.fleet.domain.model.valueobjects.EmployeeRegistrationStatus;
import com.forkdevs.driveos.platform.shared.domain.model.valueobjects.BranchId;

public record GetEmployeeRegistrationsByBranchIdAndStatusQuery(BranchId branchId, EmployeeRegistrationStatus status) {
}
