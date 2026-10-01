package com.forkdevs.driveos.platform.core.infrastructure.persistence.jpa.adapters;

import com.forkdevs.driveos.platform.core.domain.model.aggregates.Employee;
import com.forkdevs.driveos.platform.core.domain.model.valueobjects.EmployeeId;
import com.forkdevs.driveos.platform.core.domain.model.valueobjects.UserId;
import com.forkdevs.driveos.platform.core.domain.repositories.EmployeeRepository;
import com.forkdevs.driveos.platform.core.infrastructure.persistence.jpa.assemblers.EmployeePersistenceAssembler;
import com.forkdevs.driveos.platform.core.infrastructure.persistence.jpa.entities.EmployeePersistenceEntity;
import com.forkdevs.driveos.platform.core.infrastructure.persistence.jpa.repositories.EmployeePersistenceRepository;

import org.springframework.stereotype.Repository;
import java.util.Optional;

@Repository
public class EmployeeRepositoryImpl implements EmployeeRepository {

    private final EmployeePersistenceRepository employeePersistenceRepository;

    public EmployeeRepositoryImpl(EmployeePersistenceRepository employeePersistenceRepository) {
        this.employeePersistenceRepository = employeePersistenceRepository;
    }

    @Override
    public Employee save(Employee employee) {
        EmployeePersistenceEntity entity;
        if (employee.getId() != null) {
            entity = employeePersistenceRepository.findById(employee.getId().value()).orElse(new EmployeePersistenceEntity());
        } else {
            entity = new EmployeePersistenceEntity();
        }
        
        EmployeePersistenceAssembler.toEntity(employee, entity);
        EmployeePersistenceEntity savedEntity = employeePersistenceRepository.save(entity);
        return EmployeePersistenceAssembler.toDomain(savedEntity);
    }

    @Override
    public Optional<Employee> findById(EmployeeId id) {
        return employeePersistenceRepository.findById(id.value()).map(EmployeePersistenceAssembler::toDomain);
    }

    @Override
    public Optional<Employee> findByUserId(UserId userId) {
        return employeePersistenceRepository.findByUserId(userId.value()).map(EmployeePersistenceAssembler::toDomain);
    }

    @Override
    public boolean existsByUserId(UserId userId) {
        return employeePersistenceRepository.existsByUserId(userId.value());
    }

    @Override
    public Optional<Employee> findByDocumentNumber(String documentNumber) {
        return employeePersistenceRepository.findByDocumentNumber(documentNumber).map(EmployeePersistenceAssembler::toDomain);
    }

    @Override
    public void delete(Employee employee) {
        if (employee.getId() != null) {
            employeePersistenceRepository.findById(employee.getId().value()).ifPresent(employeePersistenceRepository::delete);
        }
    }
}
