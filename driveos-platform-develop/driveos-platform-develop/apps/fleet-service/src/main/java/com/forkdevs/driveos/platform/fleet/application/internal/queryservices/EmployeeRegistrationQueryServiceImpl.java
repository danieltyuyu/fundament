package com.forkdevs.driveos.platform.fleet.application.internal.queryservices;

import com.forkdevs.driveos.platform.fleet.application.queryservices.EmployeeRegistrationQueryService;
import com.forkdevs.driveos.platform.fleet.domain.model.aggregates.EmployeeRegistration;
import com.forkdevs.driveos.platform.fleet.domain.model.queries.GetEmployeeRegistrationByIdQuery;
import com.forkdevs.driveos.platform.fleet.domain.model.queries.GetEmployeeRegistrationByEmployeeIdQuery;
import com.forkdevs.driveos.platform.fleet.domain.model.queries.GetEmployeeRegistrationsByBranchIdQuery;
import com.forkdevs.driveos.platform.fleet.domain.model.queries.GetEmployeeRegistrationsByBranchIdAndStatusQuery;
import com.forkdevs.driveos.platform.fleet.domain.repositories.EmployeeRegistrationRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class EmployeeRegistrationQueryServiceImpl implements EmployeeRegistrationQueryService {

    private final EmployeeRegistrationRepository employeeRegistrationRepository;

    public EmployeeRegistrationQueryServiceImpl(EmployeeRegistrationRepository employeeRegistrationRepository) {
        this.employeeRegistrationRepository = employeeRegistrationRepository;
    }

    @Override
    public Optional<EmployeeRegistration> handle(GetEmployeeRegistrationByIdQuery query) {
        return employeeRegistrationRepository.findById(query.id());
    }

    @Override
    public Optional<EmployeeRegistration> handle(GetEmployeeRegistrationByEmployeeIdQuery query) {
        return employeeRegistrationRepository.findByEmployeeId(query.employeeId());
    }

    @Override
    public List<EmployeeRegistration> handle(GetEmployeeRegistrationsByBranchIdQuery query) {
        return employeeRegistrationRepository.findByBranchId(query.branchId());
    }

    @Override
    public List<EmployeeRegistration> handle(GetEmployeeRegistrationsByBranchIdAndStatusQuery query) {
        return employeeRegistrationRepository.findByBranchIdAndStatus(query.branchId(), query.status());
    }
}
