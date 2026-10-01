package com.forkdevs.driveos.platform.inventory.interfaces.rest.resources;

import java.util.Date;

public record ProductBatchResource(
        String batchId,
        Integer initialQuantity,
        Integer availableQuantity,
        Double acquisitionCost,
        Date createdAt
) {
}
