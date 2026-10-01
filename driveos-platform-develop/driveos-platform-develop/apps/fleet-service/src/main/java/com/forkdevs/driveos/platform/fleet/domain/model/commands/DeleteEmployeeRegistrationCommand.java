package com.forkdevs.driveos.platform.fleet.domain.model.commands;

import com.forkdevs.driveos.platform.fleet.domain.model.valueobjects.EmployeeId;

public record DeleteEmployeeRegistrationCommand(EmployeeId id) {
}
