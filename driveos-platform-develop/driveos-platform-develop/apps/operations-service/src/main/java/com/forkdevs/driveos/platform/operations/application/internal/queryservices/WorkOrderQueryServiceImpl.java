package com.forkdevs.driveos.platform.operations.application.internal.queryservices;

import com.forkdevs.driveos.platform.operations.application.queryservices.WorkOrderQueryService;
import com.forkdevs.driveos.platform.operations.domain.model.aggregates.WorkOrder;
import com.forkdevs.driveos.platform.operations.domain.model.queries.*;
import com.forkdevs.driveos.platform.operations.domain.repositories.WorkOrderRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.jdbc.core.JdbcTemplate;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * Internal application service implementing {@link WorkOrderQueryService}.
 * Provides read-only transactional access to work order aggregates.
 * @author Joel Huamani Estefanero
 */
@Service
@Transactional(readOnly = true)
public class WorkOrderQueryServiceImpl implements WorkOrderQueryService {

    private final WorkOrderRepository workOrderRepository;
    private final JdbcTemplate jdbcTemplate;

    /**
     * Constructor for WorkOrderQueryServiceImpl, injecting the WorkOrderRepository and JdbcTemplate dependency.
     * @param workOrderRepository Repository for accessing work order data from the database.
     * @param jdbcTemplate JdbcTemplate for executing SQL queries directly on organizational tables.
     */
    public WorkOrderQueryServiceImpl(WorkOrderRepository workOrderRepository, JdbcTemplate jdbcTemplate) {
        this.workOrderRepository = workOrderRepository;
        this.jdbcTemplate = jdbcTemplate;
    }

    /**
     * Handles the GetWorkOrderByIdQuery by retrieving a work order from the repository based on its unique identifier. If the work order ID is null, an IllegalArgumentException is thrown.
     * @param query The query containing the work order ID to retrieve.
     * @return An Optional containing the WorkOrder if found, or empty if not found.
     */
    @Override
    public Optional<WorkOrder> handle(GetWorkOrderByIdQuery query) {
        if (query.workOrderId() == null) {
            throw new IllegalArgumentException("operations.error.query.workOrderId.required");
        }
        return workOrderRepository.findById(query.workOrderId());
    }

    @Override
    public Optional<WorkOrder> handle(GetWorkOrderByTaskIdQuery query) {
        if (query.taskId() == null) {
            throw new IllegalArgumentException("operations.error.query.taskId.required");
        }
        return workOrderRepository.findByTaskId(query.taskId());
    }

    /**
     * Handles the GetWorkOrdersByBranchIdQuery by retrieving a list of work orders from the repository based on the branch ID. If the branch ID is null, an IllegalArgumentException is thrown.
     * @param query The query containing the branch ID to retrieve work orders for.
     * @return A list of WorkOrder objects associated with the specified branch ID.
     */
    @Override
    public List<WorkOrder> handle(GetWorkOrdersByBranchIdQuery query) {
        if (query.branchId() == null) {
            throw new IllegalArgumentException("operations.error.query.branchId.required");
        }
        return workOrderRepository.findAllByBranchId(query.branchId());
    }

    /**
     * Handles the GetWorkOrdersByVehicleIdQuery by retrieving a list of work orders from the repository based on the vehicle ID. If the vehicle ID is null, an IllegalArgumentException is thrown.
     * @param query The query containing the vehicle ID to retrieve work orders for.
     * @return A list of WorkOrder objects associated with the specified vehicle ID.
     */
    @Override
    public List<WorkOrder> handle(GetWorkOrdersByVehicleIdQuery query) {
        if (query.vehicleId() == null) {
            throw new IllegalArgumentException("operations.error.query.vehicleId.required");
        }
        return workOrderRepository.findAllByVehicleId(query.vehicleId());
    }

    @Override
    public String getBranchCode(UUID branchId) {
        return jdbcTemplate.queryForObject("SELECT code FROM branches WHERE id = ?", String.class, branchId);
    }
}