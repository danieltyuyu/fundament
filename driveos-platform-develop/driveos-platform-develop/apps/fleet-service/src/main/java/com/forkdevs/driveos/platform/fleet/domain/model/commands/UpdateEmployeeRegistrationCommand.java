package com.forkdevs.driveos.platform.fleet.domain.model.commands;

import com.forkdevs.driveos.platform.fleet.domain.model.valueobjects.EmployeeId;

import java.math.BigDecimal;

public record UpdateEmployeeRegistrationCommand(
        EmployeeId id,
        String speciality,
        String specialityName,
        BigDecimal salary
) {
}
