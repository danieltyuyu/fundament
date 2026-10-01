package com.forkdevs.driveos.platform.fleet.interfaces.rest.transform;

import com.forkdevs.driveos.platform.fleet.domain.model.aggregates.EmployeeRegistration;
import com.forkdevs.driveos.platform.fleet.interfaces.rest.resources.EmployeeRegistrationResource;

public class EmployeeRegistrationResourceFromAggregateAssembler {

    public static EmployeeRegistrationResource toResourceFromAggregate(EmployeeRegistration aggregate) {
        return new EmployeeRegistrationResource(
                aggregate.getId() != null ? aggregate.getId().value() : null,
                aggregate.getEmployeeId(),
                aggregate.getBranchId() != null ? aggregate.getBranchId().value() : null,
                aggregate.getSpeciality(),
                aggregate.getSpecialityName(),
                aggregate.getSalary(),
                aggregate.getStatus() != null ? aggregate.getStatus().value() : null,
                aggregate.getCreatedAt(),
                aggregate.getUpdatedAt(),
                aggregate.getDeletedAt()
        );
    }
}
