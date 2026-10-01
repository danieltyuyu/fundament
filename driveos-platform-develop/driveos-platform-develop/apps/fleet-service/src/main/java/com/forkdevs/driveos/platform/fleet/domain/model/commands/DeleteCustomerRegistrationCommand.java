package com.forkdevs.driveos.platform.fleet.domain.model.commands;

import java.util.UUID;

public record DeleteCustomerRegistrationCommand(UUID registrationId) {

    public DeleteCustomerRegistrationCommand {
        if (registrationId == null) {
            throw new IllegalArgumentException("Registration ID is required");
        }
    }
}

