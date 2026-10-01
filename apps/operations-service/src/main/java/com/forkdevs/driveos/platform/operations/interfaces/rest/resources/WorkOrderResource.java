package com.forkdevs.driveos.platform.operations.interfaces.rest.resources;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;
import java.util.UUID;

/**
 * Resource representing a Work Order in the REST API. This resource is used to transfer data about a Work Order between the server and the client. It includes all relevant information about the Work Order, such as its unique identifier, associated appointment, branch, vehicle, customer, internal number, status, diagnostic summary, mileage, total amount, list of tasks, and timestamps for creation and last update.
 * @param id
 * @param appointmentId
 * @param branchId
 * @param vehicleId
 * @param customerId
 * @param internalNumber
 * @param status
 * @param diagnosticSummary
 * @param mileageIn
 * @param totalAmount
 * @param tasks
 * @param createdAt
 * @param updatedAt
 * @author Joel Huamani Estefanero
 */
public record WorkOrderResource(
        UUID id,
        UUID appointmentId,
        UUID branchId,
        UUID vehicleId,
        UUID customerId,
        Integer internalNumber,
        String formattedNumber,
        String status,
        String diagnosticSummary,
        Integer mileageIn,
        BigDecimal totalAmount,
        List<WorkOrderTaskResource> tasks, // Lista de tareas internas formateadas
        Instant createdAt,
        Instant updatedAt
) {}