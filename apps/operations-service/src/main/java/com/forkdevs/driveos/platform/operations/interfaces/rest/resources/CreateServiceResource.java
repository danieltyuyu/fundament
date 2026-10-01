package com.forkdevs.driveos.platform.operations.interfaces.rest.resources;

import java.util.UUID;

public record CreateServiceResource(UUID branchId, String name, Double price) {
}
