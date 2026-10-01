package com.forkdevs.driveos.platform.inventory.application.internal.eventhandlers;

import com.forkdevs.driveos.platform.inventory.domain.model.aggregates.Product;
import com.forkdevs.driveos.platform.inventory.domain.model.events.ProductReservationCanceledEvent;
import com.forkdevs.driveos.platform.inventory.domain.model.events.ProductReservedEvent;
import com.forkdevs.driveos.platform.inventory.domain.model.events.WorkOrderPaidEvent;
import com.forkdevs.driveos.platform.inventory.domain.model.valueobjects.InventoryQuantity;
import com.forkdevs.driveos.platform.inventory.domain.repositories.ProductRepository;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
public class InventoryStockListener {
    private final ProductRepository productRepository;

    public InventoryStockListener(ProductRepository productRepository) {
        this.productRepository = productRepository;
    }

    @EventListener
    public void on(ProductReservedEvent event) {
        Optional<Product> productOpt = productRepository.findById(event.productId());
        productOpt.ifPresent(product -> {
            product.reserveStock(new InventoryQuantity(event.quantity()));
            productRepository.save(product);
        });
    }

    @EventListener
    public void on(ProductReservationCanceledEvent event) {
        Optional<Product> productOpt = productRepository.findById(event.productId());
        productOpt.ifPresent(product -> {
            product.releaseStock(new InventoryQuantity(event.quantity()));
            productRepository.save(product);
        });
    }

    @EventListener
    public void on(WorkOrderPaidEvent event) {
        // En la arquitectura definida con Operations, el stock se descuenta físicamente de los lotes 
        // al momento de hacer la reserva (ProductReservedEvent). 
        // El trigger de base de datos sync_product_stock() se encarga de actualizar el current_stock.
        // Por lo tanto, al pagar la orden, no es necesario hacer ninguna deducción adicional.
    }
}
