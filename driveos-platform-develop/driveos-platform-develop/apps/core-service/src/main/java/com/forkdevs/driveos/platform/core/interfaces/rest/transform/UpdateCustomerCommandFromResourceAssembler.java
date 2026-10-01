package com.forkdevs.driveos.platform.core.interfaces.rest.transform;

import com.forkdevs.driveos.platform.core.domain.model.commands.UpdateCustomerCommand;
import com.forkdevs.driveos.platform.core.domain.model.valueobjects.Document;
import com.forkdevs.driveos.platform.core.domain.model.valueobjects.PersonName;
import com.forkdevs.driveos.platform.core.domain.model.valueobjects.Phone;
import com.forkdevs.driveos.platform.shared.domain.model.valueobjects.CustomerId;
import com.forkdevs.driveos.platform.core.interfaces.rest.resources.UpdateCustomerResource;

import java.util.UUID;

public class UpdateCustomerCommandFromResourceAssembler {
    public static UpdateCustomerCommand toCommandFromResource(UUID customerId, UpdateCustomerResource resource) {
        return new UpdateCustomerCommand(
                new CustomerId(customerId),
                new PersonName(resource.firstName(), resource.lastName()),
                resource.businessName(),
                new Document(resource.documentType(), resource.documentNumber()),
                new Phone(resource.phone())
        );
    }
}
