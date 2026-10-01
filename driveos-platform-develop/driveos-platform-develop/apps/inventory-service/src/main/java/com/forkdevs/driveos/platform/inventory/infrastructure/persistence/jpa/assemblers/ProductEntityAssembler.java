package com.forkdevs.driveos.platform.inventory.infrastructure.persistence.jpa.assemblers;

import com.forkdevs.driveos.platform.inventory.domain.model.aggregates.Product;
import com.forkdevs.driveos.platform.inventory.domain.model.entities.ProductBatch;
import com.forkdevs.driveos.platform.inventory.domain.model.valueobjects.*;
import com.forkdevs.driveos.platform.inventory.infrastructure.persistence.jpa.entities.ProductBatchJpaEntity;
import com.forkdevs.driveos.platform.inventory.infrastructure.persistence.jpa.entities.ProductJpaEntity;
import com.forkdevs.driveos.platform.shared.domain.model.valueobjects.BranchId;

import java.util.stream.Collectors;

public class ProductEntityAssembler {
    public static ProductJpaEntity toEntity(Product product, ProductJpaEntity entity) {
        if (product == null) return null;
        if (entity == null) entity = new ProductJpaEntity();
        
        if (product.getVersion() != null) {
            entity.setId(product.getId());
            entity.setVersion(product.getVersion());
        }
        
        entity.setBranchId(product.getBranchId().value());
        entity.setCategory(product.getCategory().value());
        entity.setName(product.getName().name());
        entity.setSku(product.getSku().value());
        entity.setDescription(product.getDescription());
        entity.setCurrentSellingPrice(product.getCurrentSellingPrice());
        entity.setCurrentStock(product.getCurrentStock().value());
        entity.setMinimumStock(product.getMinimumStock());

        if (entity.getBatches() == null) {
            entity.setBatches(new java.util.ArrayList<>());
        }
        
        var productBatchIds = product.getBatches().stream().map(ProductBatch::getBatchId).collect(Collectors.toSet());
        entity.getBatches().removeIf(b -> !productBatchIds.contains(b.getId()));
        
        for (ProductBatch b : product.getBatches()) {
            var batchEntity = entity.getBatches().stream().filter(be -> be.getId() != null && be.getId().equals(b.getBatchId())).findFirst().orElse(null);
            if (batchEntity == null) {
                batchEntity = new ProductBatchJpaEntity();
                batchEntity.setProduct(entity);
                entity.getBatches().add(batchEntity);
            }
            if (b.getVersion() != null) {
                batchEntity.setVersion(b.getVersion());
            }
            batchEntity.setBranchId(product.getBranchId().value());
            batchEntity.setInitialQuantity(b.getInitialQuantity().value()); 
            batchEntity.setAvailableQuantity(b.getAvailableQuantity().value());
            batchEntity.setAcquisitionCost(b.getAcquisitionCost());
        }

        return entity;
    }

    public static Product toAggregate(ProductJpaEntity entity) {
        if (entity == null) return null;
        java.util.List<ProductBatch> batches = new java.util.ArrayList<>();
        if (entity.getBatches() != null) {
            for (ProductBatchJpaEntity be : entity.getBatches()) {
                batches.add(ProductBatch.reconstitute(
                    be.getId(), 
                    new InventoryQuantity(be.getInitialQuantity()), 
                    new InventoryQuantity(be.getAvailableQuantity()), 
                    be.getAcquisitionCost(),
                    be.getCreatedAt() != null ? java.util.Date.from(be.getCreatedAt()) : null,
                    be.getVersion()
                ));
            }
        }
        return Product.reconstitute(
            entity.getId(), 
            new BranchId(entity.getBranchId()), 
            new ProductCategory(entity.getCategory()), 
            new ProductName(entity.getName()), 
            new Sku(entity.getSku()), 
            new InventoryQuantity(entity.getCurrentStock()), 
            entity.getCurrentSellingPrice(),
            entity.getDescription(),
            entity.getMinimumStock(),
            entity.getVersion(),
            batches
        );
    }
}
