package com.forkdevs.driveos.platform.core.interfaces.rest.transform;

import com.forkdevs.driveos.platform.core.domain.model.commands.CreateBranchCommand;
import com.forkdevs.driveos.platform.core.domain.model.valueobjects.Phone;
import com.forkdevs.driveos.platform.core.domain.model.valueobjects.WorkshopId;
import com.forkdevs.driveos.platform.core.interfaces.rest.resources.CreateBranchResource;
import com.forkdevs.driveos.platform.shared.domain.model.valueobjects.Address;

public class CreateBranchCommandFromResourceAssembler {
    public static CreateBranchCommand toCommandFromResource(CreateBranchResource resource) {
        return new CreateBranchCommand(
                new WorkshopId(resource.workshopId()),
                resource.code(),
                resource.name(),
                new Address(resource.address()),
                new Phone(resource.phone())
        );
    }
}
