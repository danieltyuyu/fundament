package com.forkdevs.driveos.platform.operations.interfaces.rest;

import com.forkdevs.driveos.platform.operations.application.commandservices.WorkOrderCommandFailure;
import com.forkdevs.driveos.platform.operations.application.commandservices.WorkOrderCommandService;
import com.forkdevs.driveos.platform.operations.application.queryservices.WorkOrderQueryService;
import com.forkdevs.driveos.platform.operations.domain.model.aggregates.WorkOrder;
import com.forkdevs.driveos.platform.operations.domain.model.commands.*;
import com.forkdevs.driveos.platform.operations.domain.model.queries.GetWorkOrderByTaskIdQuery;
import com.forkdevs.driveos.platform.operations.domain.model.valueobjects.ProductId;
import com.forkdevs.driveos.platform.operations.domain.model.valueobjects.WorkOrderId;
import com.forkdevs.driveos.platform.operations.domain.model.valueobjects.WorkOrderTaskId;
import com.forkdevs.driveos.platform.operations.interfaces.rest.resources.*;
import com.forkdevs.driveos.platform.operations.interfaces.rest.transform.*;
import com.forkdevs.driveos.platform.shared.application.result.Result;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.context.MessageSource;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

/**
 * REST controller for managing work order tasks and their associated products.
 * Provides endpoints under /api/v1/work-order-tasks for adding, updating, and removing task products,
 * as well as task state transitions (start, complete, reopen).
 * It delegates lookup to WorkOrderQueryService using task IDs, maintaining a clean 2-level nesting REST API.
 * @author Joel Huamani Estefanero
 */
@RestController
@RequestMapping(value = "/api/v1/work-order-tasks", produces = "application/json")
@Tag(name = "Work Order Tasks", description = "Endpoints for managing work order tasks and their products")
public class WorkOrderTasksController {

    private final WorkOrderCommandService commandService;
    private final WorkOrderQueryService queryService;
    private final MessageSource messageSource;

    public WorkOrderTasksController(WorkOrderCommandService commandService,
                                    WorkOrderQueryService queryService,
                                    MessageSource messageSource) {
        this.commandService = commandService;
        this.queryService = queryService;
        this.messageSource = messageSource;
    }

    /**
     * Helper private method to transform command Result mapping branch short code formatting.
     */
    private ResponseEntity<?> toResponse(Result<WorkOrder, WorkOrderCommandFailure> result) {
        return toResponse(result, HttpStatus.OK);
    }

    private ResponseEntity<?> toResponse(Result<WorkOrder, WorkOrderCommandFailure> result, HttpStatus status) {
        String branchCode = result.success()
                .map(wo -> queryService.getBranchCode(wo.getBranchId().value()))
                .orElse("WO");
        return ResponseEntityFromWorkOrderCommandResultAssembler.toResponseEntityFromResult(result, messageSource, branchCode, status);
    }

    /**
     * Helper method to resolve WorkOrderId from taskId.
     */
    private UUID getWorkOrderIdByTaskId(UUID taskId) {
        return queryService.handle(new GetWorkOrderByTaskIdQuery(new WorkOrderTaskId(taskId)))
                .map(workOrder -> workOrder.getId().value())
                .orElseThrow(() -> new IllegalArgumentException("operations.error.workOrder.notFoundForTask"));
    }

    @PostMapping("/{taskId}/products")
    @Operation(summary = "Add an inventory product/part to a task", description = "Adds a product from inventory to a specific mechanic task")
    public ResponseEntity<?> addProductToTask(@PathVariable UUID taskId,
                                              @Valid @RequestBody AddProductResource resource) {
        try {
            UUID id = getWorkOrderIdByTaskId(taskId);
            var command = WorkOrderCommandFromResourceAssembler.toCommandFromResource(id, taskId, resource);
            var result = commandService.handle(command);
            return toResponse(result, HttpStatus.CREATED);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        }
    }

    @PutMapping("/{taskId}/products/{productId}")
    @Operation(summary = "Update a product's quantity in a task", description = "Updates the quantity of a product used in a specific mechanic task")
    public ResponseEntity<?> updateProductQuantityInTask(@PathVariable UUID taskId,
                                                         @PathVariable UUID productId,
                                                         @Valid @RequestBody UpdateProductQuantityInTaskResource resource) {
        try {
            UUID id = getWorkOrderIdByTaskId(taskId);
            var command = WorkOrderCommandFromResourceAssembler.toCommandFromResource(id, taskId, productId, resource);
            var result = commandService.handle(command);
            return toResponse(result);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        }
    }

    @DeleteMapping("/{taskId}/products/{productId}")
    @Operation(summary = "Remove a product/part from a task", description = "Removes a product from a task, releasing its stock reservation")
    public ResponseEntity<?> removeProductFromTask(@PathVariable UUID taskId,
                                                   @PathVariable UUID productId) {
        try {
            UUID id = getWorkOrderIdByTaskId(taskId);
            var command = new RemoveProductFromTaskCommand(new WorkOrderId(id), new WorkOrderTaskId(taskId), new ProductId(productId));
            var result = commandService.handle(command);
            return toResponse(result);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        }
    }

    @PostMapping("/{taskId}/start")
    @Operation(summary = "Start executing a task", description = "Sets the task status to DOING and captures the startedAt timestamp")
    public ResponseEntity<?> startTask(@PathVariable UUID taskId) {
        try {
            UUID id = getWorkOrderIdByTaskId(taskId);
            var command = new StartTaskCommand(new WorkOrderId(id), new WorkOrderTaskId(taskId));
            var result = commandService.handle(command);
            return toResponse(result);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        }
    }

    @PostMapping("/{taskId}/complete")
    @Operation(summary = "Complete a task", description = "Sets the task status to COMPLETED and captures the completedAt timestamp")
    public ResponseEntity<?> completeTask(@PathVariable UUID taskId) {
        try {
            UUID id = getWorkOrderIdByTaskId(taskId);
            var command = new CompleteTaskCommand(new WorkOrderId(id), new WorkOrderTaskId(taskId));
            var result = commandService.handle(command);
            return toResponse(result);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        }
    }

    @PostMapping("/{taskId}/reopen")
    @Operation(summary = "Reopen a completed task", description = "Returns the task to DOING, clears completedAt, and keeps stock reserved")
    public ResponseEntity<?> reopenTask(@PathVariable UUID taskId) {
        try {
            UUID id = getWorkOrderIdByTaskId(taskId);
            var command = new ReopenTaskCommand(new WorkOrderId(id), new WorkOrderTaskId(taskId));
            var result = commandService.handle(command);
            return toResponse(result);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        }
    }
}
