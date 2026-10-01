package com.forkdevs.driveos.platform.core.interfaces.rest.transform;

import com.forkdevs.driveos.platform.core.domain.model.aggregates.Customer;
import com.forkdevs.driveos.platform.core.interfaces.rest.resources.CustomerResource;

public class CustomerResourceFromEntityAssembler {
    public static CustomerResource toResourceFromEntity(Customer entity) {
        return new CustomerResource(
                entity.getId() != null ? entity.getId().value() : null,
                entity.getUserId() != null ? entity.getUserId().value() : null,
                entity.isCorporate(),
                entity.getName() != null ? entity.getName().firstName() : null,
                entity.getName() != null ? entity.getName().lastName() : null,
                entity.getBusinessName(),
                entity.getDocument() != null ? entity.getDocument().getDocumentType().name() : null,
                entity.getDocument() != null ? entity.getDocument().getDocumentNumber() : null,
                entity.getPhone() != null ? entity.getPhone().value() : null
        );
    }
}
