package com.forkdevs.driveos.platform.core.domain.model.commands;

import com.forkdevs.driveos.platform.core.domain.model.valueobjects.EmployeeId;

public record DeleteEmployeeCommand(EmployeeId employeeId) {
}
