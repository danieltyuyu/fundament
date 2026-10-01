package com.forkdevs.driveos.platform.inventory.application.internal.commandservices;

import com.forkdevs.driveos.platform.inventory.application.commandservices.ProductCommandService;
import com.forkdevs.driveos.platform.inventory.domain.model.aggregates.Product;
import com.forkdevs.driveos.platform.inventory.domain.model.commands.CreateProductCommand;
import com.forkdevs.driveos.platform.inventory.domain.repositories.ProductRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;
import java.util.UUID;

@Service
public class ProductCommandServiceImpl implements ProductCommandService {
    private final ProductRepository productRepository;

    public ProductCommandServiceImpl(ProductRepository productRepository) {
        this.productRepository = productRepository;
    }

    @Override
    @Transactional
    public Optional<Product> handle(CreateProductCommand command) {
        Product product = new Product(UUID.randomUUID(), command.branchId(), command.category(), command.name(), command.sku(), command.salePrice(), command.description(), command.minimumStock().value());
        var savedProduct = productRepository.save(product);
        return Optional.of(savedProduct);
    }

    @Override
    @Transactional
    public Optional<com.forkdevs.driveos.platform.inventory.domain.model.entities.ProductBatch> handle(com.forkdevs.driveos.platform.inventory.domain.model.commands.AddBatchToProductCommand command) {
        var product = productRepository.findById(command.productId());
        if (product.isEmpty()) {
            return Optional.empty();
        }
        var batch = new com.forkdevs.driveos.platform.inventory.domain.model.entities.ProductBatch(
                UUID.randomUUID(),
                command.quantity(),
                command.acquisitionCost()
        );
        product.get().addBatch(batch);
        var savedProduct = productRepository.save(product.get());
        var savedBatch = savedProduct.getBatches().get(savedProduct.getBatches().size() - 1);
        return Optional.of(savedBatch);
    }

    @Override
    @Transactional
    public Optional<Product> handle(com.forkdevs.driveos.platform.inventory.domain.model.commands.UpdateProductCommand command) {
        var product = productRepository.findById(command.productId());
        if (product.isEmpty()) {
            return Optional.empty();
        }
        product.get().updateDetails(command.name(), command.category(), command.sku(), command.salePrice(), command.description(), command.minimumStock().value());
        var savedProduct = productRepository.save(product.get());
        return Optional.of(savedProduct);
    }

    @Override
    @Transactional
    public void handle(com.forkdevs.driveos.platform.inventory.domain.model.commands.DeleteProductCommand command) {
        if (!productRepository.existsById(command.productId())) {
            throw new IllegalArgumentException("inventory.error.product.notFound");
        }
        productRepository.deleteById(command.productId());
    }
}
