package com.forkdevs.driveos.platform.billing.application.outboundservices;

import java.util.Optional;
import java.util.UUID;

public interface IssuerQueryGateway {
    Optional<String> getIssuerTaxIdByBranchId(UUID branchId);
}
