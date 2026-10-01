package com.forkdevs.driveos.platform.core.interfaces.rest.resources;

import java.util.UUID;

public record AssignSubscriptionResource(
        UUID planId,
        String billingCycle,
        String cardNumber,
        String cardHolderName,
        String expirationDate,
        String cvv
) {
}
