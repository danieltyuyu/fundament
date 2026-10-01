package com.forkdevs.driveos.platform.core.interfaces.rest.resources;

import java.util.UUID;

public record IssuerTaxIdResource(
        UUID branchId,
        String taxId
) {
}
