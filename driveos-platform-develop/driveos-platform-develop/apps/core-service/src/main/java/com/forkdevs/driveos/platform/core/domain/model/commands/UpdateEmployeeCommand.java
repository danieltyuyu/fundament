package com.forkdevs.driveos.platform.core.domain.model.commands;

import com.forkdevs.driveos.platform.core.domain.model.valueobjects.Document;
import com.forkdevs.driveos.platform.core.domain.model.valueobjects.EmployeeId;
import com.forkdevs.driveos.platform.core.domain.model.valueobjects.PersonName;
import com.forkdevs.driveos.platform.core.domain.model.valueobjects.Phone;

public record UpdateEmployeeCommand(
        EmployeeId employeeId,
        PersonName name,
        Document document,
        Phone phone
) {
}
