package com.forkdevs.driveos.platform.inventory.interfaces.rest;

import com.forkdevs.driveos.platform.inventory.application.commandservices.ProductCommandService;
import com.forkdevs.driveos.platform.inventory.application.queryservices.ProductQueryService;
import com.forkdevs.driveos.platform.inventory.domain.model.aggregates.Product;
import com.forkdevs.driveos.platform.inventory.domain.model.commands.CreateProductCommand;
import com.forkdevs.driveos.platform.inventory.domain.model.queries.GetProductsByBranchIdQuery;
import com.forkdevs.driveos.platform.inventory.domain.model.valueobjects.*;
import com.forkdevs.driveos.platform.inventory.interfaces.rest.resources.CreateProductResource;
import com.forkdevs.driveos.platform.inventory.interfaces.rest.resources.ProductResource;
import com.forkdevs.driveos.platform.inventory.interfaces.rest.transform.ProductResourceFromAggregateAssembler;
import com.forkdevs.driveos.platform.shared.domain.model.valueobjects.BranchId;
import com.forkdevs.driveos.platform.shared.domain.model.valueobjects.Money;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * REST controller for managing inventory products.
 * Uses CQRS pattern: delegates commands to ProductCommandService
 * and queries to ProductQueryService.
 * @author Adiel Sanchez
 */
@RestController
@RequestMapping(value = "/api/v1/inventory/products", produces = "application/json")
@Tag(name = "Inventory Products", description = "Endpoints for managing products in the inventory")
public class ProductsController {
    private final ProductCommandService productCommandService;
    private final ProductQueryService productQueryService;

    public ProductsController(ProductCommandService productCommandService,
                              ProductQueryService productQueryService) {
        this.productCommandService = productCommandService;
        this.productQueryService = productQueryService;
    }

    @PostMapping
    @Operation(summary = "Create a new Product", description = "Creates a new product in the inventory for a specific branch")
    public ResponseEntity<ProductResource> createProduct(@RequestBody CreateProductResource resource) {
        CreateProductCommand command = new CreateProductCommand(
                new BranchId(java.util.UUID.fromString(resource.branchId())),
                new ProductCategory(resource.category()),
                new ProductName(resource.name()),
                new Sku(resource.sku()),
                resource.description(),
                new Money(java.math.BigDecimal.valueOf(resource.salePrice())),
                new InventoryQuantity(resource.minimumStock())
        );

        Optional<Product> product = productCommandService.handle(command);
        return product.map(p -> new ResponseEntity<>(ProductResourceFromAggregateAssembler.toResourceFromAggregate(p), HttpStatus.CREATED))
                .orElseGet(() -> ResponseEntity.badRequest().build());
    }

    @GetMapping
    @Operation(summary = "Get all products for a branch", description = "Retrieves all products in the inventory belonging to the specified branch (multi-tenant query)")
    public ResponseEntity<List<ProductResource>> getProductsByBranch(@RequestParam UUID branchId) {
        var query = new GetProductsByBranchIdQuery(new BranchId(branchId));
        List<Product> products = productQueryService.handle(query);
        List<ProductResource> resources = products.stream()
                .map(ProductResourceFromAggregateAssembler::toResourceFromAggregate)
                .toList();
        return ResponseEntity.ok(resources);
    }

    @PostMapping("/{productId}/batches")
    @Operation(summary = "Add a batch to a product", description = "Adds a physical batch to an existing product, increasing its current stock")
    public ResponseEntity<com.forkdevs.driveos.platform.inventory.interfaces.rest.resources.ProductBatchResource> addBatchToProduct(
            @PathVariable UUID productId,
            @RequestBody com.forkdevs.driveos.platform.inventory.interfaces.rest.resources.AddBatchToProductResource resource) {

        var command = new com.forkdevs.driveos.platform.inventory.domain.model.commands.AddBatchToProductCommand(
                productId,
                new InventoryQuantity(resource.quantity()),
                new Money(java.math.BigDecimal.valueOf(resource.acquisitionCost()))
        );

        var productBatch = productCommandService.handle(command);
        return productBatch.map(batch -> new ResponseEntity<>(
                com.forkdevs.driveos.platform.inventory.interfaces.rest.transform.ProductBatchResourceFromEntityAssembler.toResourceFromEntity(batch),
                HttpStatus.CREATED
        )).orElseGet(() -> ResponseEntity.notFound().build());
    }

    @GetMapping("/{productId}")
    @Operation(summary = "Get product details by ID", description = "Retrieves all details for a product including its associated batches")
    public ResponseEntity<com.forkdevs.driveos.platform.inventory.interfaces.rest.resources.ProductDetailsResource> getProductById(@PathVariable UUID productId) {
        var query = new com.forkdevs.driveos.platform.inventory.domain.model.queries.GetProductByIdQuery(productId);
        var product = productQueryService.handle(query);
        
        return product.map(p -> ResponseEntity.ok(
                com.forkdevs.driveos.platform.inventory.interfaces.rest.transform.ProductDetailsResourceFromAggregateAssembler.toResourceFromAggregate(p)
        )).orElseGet(() -> ResponseEntity.notFound().build());
    }

    @PutMapping("/{productId}")
    @Operation(summary = "Update product details", description = "Updates the basic details of a product (Name, Category, SKU)")
    public ResponseEntity<ProductResource> updateProduct(
            @PathVariable UUID productId,
            @RequestBody com.forkdevs.driveos.platform.inventory.interfaces.rest.resources.UpdateProductResource resource) {
            
        var command = new com.forkdevs.driveos.platform.inventory.domain.model.commands.UpdateProductCommand(
                productId,
                new com.forkdevs.driveos.platform.inventory.domain.model.valueobjects.ProductName(resource.name()),
                new com.forkdevs.driveos.platform.inventory.domain.model.valueobjects.ProductCategory(resource.category().toUpperCase()),
                new com.forkdevs.driveos.platform.inventory.domain.model.valueobjects.Sku(resource.sku()),
                resource.description(),
                new Money(java.math.BigDecimal.valueOf(resource.salePrice())),
                new InventoryQuantity(resource.minimumStock())
        );
        
        var updatedProduct = productCommandService.handle(command);
        
        return updatedProduct.map(product -> ResponseEntity.ok(
                ProductResourceFromAggregateAssembler.toResourceFromAggregate(product)
        )).orElseGet(() -> ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{productId}")
    @Operation(summary = "Delete a product", description = "Deletes a product and all its associated batches")
    public ResponseEntity<?> deleteProduct(@PathVariable UUID productId) {
        var command = new com.forkdevs.driveos.platform.inventory.domain.model.commands.DeleteProductCommand(productId);
        try {
            productCommandService.handle(command);
            return ResponseEntity.noContent().build();
        } catch (IllegalArgumentException e) {
            return ResponseEntity.notFound().build();
        }
    }
}
