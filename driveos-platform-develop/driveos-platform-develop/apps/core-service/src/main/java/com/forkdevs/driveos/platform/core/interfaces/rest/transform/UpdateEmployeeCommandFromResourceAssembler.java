package com.forkdevs.driveos.platform.core.interfaces.rest.transform;

import com.forkdevs.driveos.platform.core.domain.model.commands.UpdateEmployeeCommand;
import com.forkdevs.driveos.platform.core.domain.model.valueobjects.Document;
import com.forkdevs.driveos.platform.core.domain.model.valueobjects.EmployeeId;
import com.forkdevs.driveos.platform.core.domain.model.valueobjects.PersonName;
import com.forkdevs.driveos.platform.core.domain.model.valueobjects.Phone;
import com.forkdevs.driveos.platform.core.interfaces.rest.resources.UpdateEmployeeResource;

import java.util.UUID;

public class UpdateEmployeeCommandFromResourceAssembler {
    public static UpdateEmployeeCommand toCommandFromResource(UUID employeeId, UpdateEmployeeResource resource) {
        return new UpdateEmployeeCommand(
                new EmployeeId(employeeId),
                new PersonName(resource.firstName(), resource.lastName()),
                new Document(resource.documentType(), resource.documentNumber()),
                new Phone(resource.phone())
        );
    }
}
