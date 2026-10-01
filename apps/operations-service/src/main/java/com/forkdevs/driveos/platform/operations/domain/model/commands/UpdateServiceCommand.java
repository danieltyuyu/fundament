package com.forkdevs.driveos.platform.operations.domain.model.commands;

import com.forkdevs.driveos.platform.operations.domain.model.valueobjects.ServiceId;
import com.forkdevs.driveos.platform.shared.domain.model.valueobjects.Money;

/**
 * Command to update an existing service with new name and price.
 * @param serviceId the unique identifier of the service to be updated
 * @param name the new name of the service
 * @param price the new price of the service
 */
public record UpdateServiceCommand(ServiceId serviceId, String name, Money price) {
     public UpdateServiceCommand {
         if (name == null || name.isBlank()) throw new IllegalArgumentException("core.error.name.required");
     }
}
