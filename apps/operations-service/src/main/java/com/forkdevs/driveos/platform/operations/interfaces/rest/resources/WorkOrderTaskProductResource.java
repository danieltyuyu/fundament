package com.forkdevs.driveos.platform.operations.interfaces.rest.resources;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

/**
 * Resource representing a Product associated with a Task in a Work Order. This resource is used to transfer data about the product, including its identifier, the branch it belongs to, the quantity used in the task, the unit price, the total amount for that quantity, and the timestamp of when it was created.
 * @param id
 * @param productId
 * @param branchId
 * @param quantity
 * @param unitPrice
 * @param totalAmount
 * @param createdAt
 * @author Joel Huamani Estefanero
 */
public record WorkOrderTaskProductResource(
        UUID id,
        UUID productId,
        UUID branchId,
        Integer quantity,
        BigDecimal unitPrice,
        BigDecimal totalAmount,
        Instant createdAt
) {}