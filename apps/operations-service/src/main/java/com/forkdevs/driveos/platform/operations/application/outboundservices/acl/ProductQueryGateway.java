package com.forkdevs.driveos.platform.operations.application.outboundservices.acl;

import com.forkdevs.driveos.platform.shared.domain.model.valueobjects.Money;

import java.util.Optional;
import java.util.UUID;

public interface ProductQueryGateway {
    Optional<ProductInfo> findProductById(UUID productId);

    record ProductInfo(UUID id, Money currentSellingPrice) {}
}
