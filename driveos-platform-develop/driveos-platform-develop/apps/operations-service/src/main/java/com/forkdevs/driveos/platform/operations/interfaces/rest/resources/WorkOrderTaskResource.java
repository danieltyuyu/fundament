package com.forkdevs.driveos.platform.operations.interfaces.rest.resources;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;
import java.util.UUID;

/**
 * Resource representing a task within a work order, including its details and associated products. This resource is used to transfer data about a specific task in the context of a work order, providing information such as the assigned mechanic, status, description, price, and timestamps for when the task was started and completed. Additionally, it includes a list of products (repuestos) that are associated with this task.
 * @param id
 * @param serviceId
 * @param branchId
 * @param assignedMechanicId
 * @param status
 * @param description
 * @param price
 * @param startedAt
 * @param completedAt
 * @param products
 * @param createdAt
 * @author Joel Huamani Estefanero
 */
public record WorkOrderTaskResource(
        UUID id,
        UUID serviceId,
        UUID branchId,
        UUID assignedMechanicId,
        String status,
        String description,
        BigDecimal price, // Precio total acumulado de la tarea (Mano de obra + Insumos)
        Instant startedAt,
        Instant completedAt,
        List<WorkOrderTaskProductResource> products, // Repuestos anidados en esta tarea
        Instant createdAt
) {}