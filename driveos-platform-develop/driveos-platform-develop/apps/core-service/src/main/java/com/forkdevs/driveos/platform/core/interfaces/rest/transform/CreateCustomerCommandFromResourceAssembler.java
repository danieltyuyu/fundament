package com.forkdevs.driveos.platform.core.interfaces.rest.transform;

import com.forkdevs.driveos.platform.core.domain.model.commands.CreateCustomerCommand;
import com.forkdevs.driveos.platform.core.domain.model.valueobjects.Document;
import com.forkdevs.driveos.platform.core.domain.model.valueobjects.PersonName;
import com.forkdevs.driveos.platform.core.domain.model.valueobjects.Phone;
import com.forkdevs.driveos.platform.core.domain.model.valueobjects.UserId;
import com.forkdevs.driveos.platform.core.interfaces.rest.resources.CreateCustomerResource;

public class CreateCustomerCommandFromResourceAssembler {
    public static CreateCustomerCommand toCommandFromResource(CreateCustomerResource resource) {
        return new CreateCustomerCommand(
                new UserId(resource.userId()),
                resource.isCorporate(),
                !resource.isCorporate() ? new PersonName(resource.firstName(), resource.lastName()) : null,
                resource.businessName(),
                new Document(resource.documentType(), resource.documentNumber()),
                new Phone(resource.phone())
        );
    }
}
