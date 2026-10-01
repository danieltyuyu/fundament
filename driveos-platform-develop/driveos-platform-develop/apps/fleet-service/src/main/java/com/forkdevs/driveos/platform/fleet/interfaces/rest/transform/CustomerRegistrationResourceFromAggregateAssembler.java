package com.forkdevs.driveos.platform.fleet.interfaces.rest.transform;

import com.forkdevs.driveos.platform.fleet.domain.model.aggregates.CustomerRegistration;
import com.forkdevs.driveos.platform.fleet.interfaces.rest.resources.CustomerRegistrationResource;

public class CustomerRegistrationResourceFromAggregateAssembler {

    public static CustomerRegistrationResource toResourceFromAggregate(CustomerRegistration aggregate) {
        return new CustomerRegistrationResource(
                aggregate.getId() != null ? aggregate.getId().value() : null,
                aggregate.getBranchId() != null ? aggregate.getBranchId().value() : null,
                aggregate.getCustomerId(),
                aggregate.getStatus() != null ? aggregate.getStatus().value() : null,
                aggregate.getCreatedAt(),
                aggregate.getDeletedAt()
        );
    }
}

