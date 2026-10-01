package com.forkdevs.driveos.platform.core.domain.repositories;

import com.forkdevs.driveos.platform.core.domain.model.aggregates.Employee;
import com.forkdevs.driveos.platform.core.domain.model.valueobjects.EmployeeId;
import com.forkdevs.driveos.platform.core.domain.model.valueobjects.UserId;

import java.util.Optional;

public interface EmployeeRepository {
    Employee save(Employee employee);
    Optional<Employee> findById(EmployeeId id);
    Optional<Employee> findByUserId(UserId userId);
    boolean existsByUserId(UserId userId);
    Optional<Employee> findByDocumentNumber(String documentNumber);
    void delete(Employee employee);
}
