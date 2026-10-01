package com.forkdevs.driveos.platform.core.application.queryservices;

import com.forkdevs.driveos.platform.core.domain.model.aggregates.Employee;
import com.forkdevs.driveos.platform.core.domain.model.queries.GetEmployeeByIdQuery;

import com.forkdevs.driveos.platform.core.domain.model.queries.GetEmployeeByUserIdQuery;
import com.forkdevs.driveos.platform.core.domain.model.queries.GetEmployeeByDocumentNumberQuery;

import java.util.Optional;

public interface EmployeeQueryService {
    Optional<Employee> handle(GetEmployeeByIdQuery query);
    Optional<Employee> handle(GetEmployeeByUserIdQuery query);
    Optional<Employee> handle(GetEmployeeByDocumentNumberQuery query);
}
