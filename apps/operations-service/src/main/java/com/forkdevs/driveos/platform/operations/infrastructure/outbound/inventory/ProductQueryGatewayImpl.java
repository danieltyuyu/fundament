package com.forkdevs.driveos.platform.operations.infrastructure.outbound.inventory;

import com.forkdevs.driveos.platform.operations.application.outboundservices.acl.ProductQueryGateway;
import com.forkdevs.driveos.platform.shared.domain.model.valueobjects.Money;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.util.Optional;
import java.util.UUID;

@Component
public class ProductQueryGatewayImpl implements ProductQueryGateway {

    private final RestTemplate restTemplate;
    private final String inventoryServiceUrl;

    public ProductQueryGatewayImpl(
            @Value("${inventory.service.url:http://localhost:8085}") String inventoryServiceUrl) {
        this.restTemplate = new RestTemplate();
        this.inventoryServiceUrl = inventoryServiceUrl;
    }

    @Override
    public Optional<ProductInfo> findProductById(UUID productId) {
        try {
            String url = inventoryServiceUrl + "/api/v1/products/" + productId;
            ProductResponse response = restTemplate.getForObject(url, ProductResponse.class);
            if (response != null && response.unitPrice() != null) {
                return Optional.of(new ProductInfo(productId, response.unitPrice()));
            }
        } catch (Exception ignored) {
        }
        return Optional.empty();
    }

    private record ProductResponse(UUID id, Money unitPrice, String name, String category) {}
}
