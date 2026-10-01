package com.forkdevs.driveos.platform.fleet.domain.model.aggregates;

import com.forkdevs.driveos.platform.fleet.domain.model.valueobjects.EmployeeId;
import com.forkdevs.driveos.platform.fleet.domain.model.valueobjects.EmployeeRegistrationStatus;
import com.forkdevs.driveos.platform.shared.domain.model.aggregates.AbstractDomainAggregateRoot;
import com.forkdevs.driveos.platform.shared.domain.model.valueobjects.BranchId;
import lombok.Getter;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

@Getter
public class EmployeeRegistration extends AbstractDomainAggregateRoot<EmployeeRegistration> {

    private EmployeeId id;
    private UUID employeeId;
    private BranchId branchId;
    private String speciality;
    private String specialityName;
    private BigDecimal salary;
    private EmployeeRegistrationStatus status;
    private Instant createdAt;
    private Instant updatedAt;
    private Instant deletedAt;

    protected EmployeeRegistration() {
    }

    public EmployeeRegistration(UUID employeeId, BranchId branchId,
            String speciality, String specialityName, BigDecimal salary) {
        this.id = new EmployeeId(UUID.randomUUID());
        this.employeeId = employeeId;
        this.branchId = branchId;
        this.speciality = speciality;
        this.specialityName = specialityName;
        this.salary = salary;
        this.status = EmployeeRegistrationStatus.ACTIVE;
        this.createdAt = Instant.now();
        this.updatedAt = Instant.now();
    }

    public EmployeeRegistration(EmployeeId id, UUID employeeId, BranchId branchId,
            String speciality, String specialityName, BigDecimal salary,
            EmployeeRegistrationStatus status,
            Instant createdAt, Instant updatedAt, Instant deletedAt) {
        this.id = id;
        this.employeeId = employeeId;
        this.branchId = branchId;
        this.speciality = speciality;
        this.specialityName = specialityName;
        this.salary = salary;
        this.status = status;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
        this.deletedAt = deletedAt;
    }

    public void update(String speciality, String specialityName, BigDecimal salary) {
        this.speciality = speciality;
        this.specialityName = specialityName;
        this.salary = salary;
        this.updatedAt = Instant.now();
    }

    public void deactivate() {
        this.status = EmployeeRegistrationStatus.INACTIVE;
        this.updatedAt = Instant.now();
        this.deletedAt = Instant.now();
    }

}
