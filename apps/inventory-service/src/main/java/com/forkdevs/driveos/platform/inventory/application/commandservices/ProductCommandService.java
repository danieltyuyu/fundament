package com.forkdevs.driveos.platform.inventory.application.commandservices;

import com.forkdevs.driveos.platform.inventory.domain.model.aggregates.Product;
import com.forkdevs.driveos.platform.inventory.domain.model.commands.AddBatchToProductCommand;
import com.forkdevs.driveos.platform.inventory.domain.model.commands.CreateProductCommand;
import com.forkdevs.driveos.platform.inventory.domain.model.commands.DeleteProductCommand;
import com.forkdevs.driveos.platform.inventory.domain.model.commands.UpdateProductCommand;
import com.forkdevs.driveos.platform.inventory.domain.model.entities.ProductBatch;

import java.util.Optional;

public interface ProductCommandService {
    Optional<Product> handle(CreateProductCommand command);
    Optional<ProductBatch> handle(AddBatchToProductCommand command);
    Optional<Product> handle(UpdateProductCommand command);
    void handle(DeleteProductCommand command);
}
