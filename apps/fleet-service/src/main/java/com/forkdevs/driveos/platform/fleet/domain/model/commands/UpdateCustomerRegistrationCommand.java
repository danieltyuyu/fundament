package com.forkdevs.driveos.platform.fleet.domain.model.commands;

import com.forkdevs.driveos.platform.fleet.domain.model.valueobjects.CustomerRegistrationStatus;

import java.util.UUID;

public record UpdateCustomerRegistrationCommand(
        UUID registrationId,
        CustomerRegistrationStatus status
) {
    public UpdateCustomerRegistrationCommand {
        if (registrationId == null) throw new IllegalArgumentException("Registration ID is required");
        if (status == null) throw new IllegalArgumentException("Status is required");
    }
}

