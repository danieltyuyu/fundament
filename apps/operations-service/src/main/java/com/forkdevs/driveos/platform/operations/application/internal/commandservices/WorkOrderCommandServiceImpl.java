package com.forkdevs.driveos.platform.operations.application.internal.commandservices;

import com.forkdevs.driveos.platform.operations.application.commandservices.WorkOrderCommandFailure;
import com.forkdevs.driveos.platform.operations.application.commandservices.WorkOrderCommandService;
import com.forkdevs.driveos.platform.operations.domain.model.aggregates.WorkOrder;
import com.forkdevs.driveos.platform.operations.domain.model.commands.*;
import com.forkdevs.driveos.platform.operations.domain.model.valueobjects.WorkOrderId;
import com.forkdevs.driveos.platform.operations.domain.repositories.WorkOrderRepository;
import com.forkdevs.driveos.platform.operations.domain.repositories.ServiceRepository;
import com.forkdevs.driveos.platform.operations.application.outboundservices.acl.ProductQueryGateway;
import com.forkdevs.driveos.platform.shared.application.result.Result;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Internal application service implementing {@link WorkOrderCommandService}.
 * Orchestrates use cases using the domain model and work order repository inside atomic transactions.
 * @author Joel Huamani Estefanero
 */
@Service
public class WorkOrderCommandServiceImpl implements WorkOrderCommandService {

    private final WorkOrderRepository workOrderRepository;
    private final ServiceRepository serviceRepository;
    private final ProductQueryGateway productQueryGateway;

    /**
     * Constructor injection of the dependencies
     */
    public WorkOrderCommandServiceImpl(WorkOrderRepository workOrderRepository, ServiceRepository serviceRepository, ProductQueryGateway productQueryGateway) {
        this.workOrderRepository = workOrderRepository;
        this.serviceRepository = serviceRepository;
        this.productQueryGateway = productQueryGateway;
    }

    /**
     * Handles the UpdateWorkOrderDetailsCommand by loading the Work Order aggregate, invoking the updateDetails method on it, and saving the changes.
     * @param command The command object containing the Work Order ID and the new diagnostic and mileage details to be updated.
     * @return A Result object containing either the updated Work Order on success or a WorkOrderCommandFailure on failure, with appropriate error handling for not found and invalid state scenarios.
     */
    @Override
    @Transactional
    public Result<WorkOrder, WorkOrderCommandFailure> handle(UpdateWorkOrderDetailsCommand command) {
        try {
            WorkOrder workOrder = findWorkOrderOrThrow(command.workOrderId());

            workOrder.updateDetails(command.diagnosticSummary(), command.mileageIn());

            WorkOrder savedWorkOrder = workOrderRepository.save(workOrder);
            return Result.success(savedWorkOrder);
        } catch (IllegalArgumentException e) {
            return Result.failure(new WorkOrderCommandFailure.NotFound(e.getMessage()));
        } catch (IllegalStateException e) {
            return Result.failure(new WorkOrderCommandFailure.InvalidState(e.getMessage()));
        }
    }

    /**
     * Handles the CreateWorkOrderCommand by validating business rules (such as ensuring no duplicate work order for the same appointment), instantiating a new Work Order aggregate with the provided details, and saving it to the repository. The method returns a Result object that encapsulates either the newly created Work Order on success or a WorkOrderCommandFailure on failure, with specific handling for duplicate entries and validation errors.
     * @param command The command object containing all necessary information to create a new Work Order.
     * @return A Result object containing either the created Work Order on success or a WorkOrderCommandFailure on failure, with appropriate error handling for duplicate work orders and validation issues.
     */
    @Override
    @Transactional
    public Result<WorkOrder, WorkOrderCommandFailure> handle(CreateWorkOrderCommand command) {
        try {
            if (workOrderRepository.existsByAppointmentId(command.appointmentId())) {
                return Result.failure(new WorkOrderCommandFailure.Duplicate(
                        "operations.error.workOrder.alreadyExistsForAppointment"));
            }

            int nextInternalNumber = workOrderRepository.findMaxInternalNumberByBranchId(command.branchId()) + 1;

            WorkOrder workOrder = new WorkOrder(
                    command.appointmentId(),
                    command.branchId(),
                    command.vehicleId(),
                    command.customerId(),
                    nextInternalNumber,
                    command.diagnosticSummary(),
                    command.mileageIn()
            );

            WorkOrder savedWorkOrder = workOrderRepository.save(workOrder);
            return Result.success(savedWorkOrder);

        } catch (IllegalArgumentException e) {
            return Result.failure(new WorkOrderCommandFailure.NotFound(e.getMessage()));
        } catch (IllegalStateException e) {
            return Result.failure(new WorkOrderCommandFailure.InvalidState(e.getMessage()));
        }
    }

    /**
     * Handles the AddTaskToWorkOrderCommand by loading the specified Work Order aggregate, invoking the addTask method with the provided task details, and saving the updated aggregate back to the repository. The method returns a Result object that encapsulates either the updated Work Order on success or a WorkOrderCommandFailure on failure, with specific handling for not found and invalid state scenarios.
     * @param command The command object containing the Work Order ID and the details of the Task to be added (service, mechanic, description, labor price).
     * @return A Result object containing either the updated Work Order on success or a WorkOrderCommandFailure on failure, with appropriate error handling for not found and invalid state scenarios.
     */
    @Override
    @Transactional
    public Result<WorkOrder, WorkOrderCommandFailure> handle(AddTaskToWorkOrderCommand command) {
        try {
            WorkOrder workOrder = findWorkOrderOrThrow(command.workOrderId());
            var service = serviceRepository.findById(command.serviceId())
                    .orElseThrow(() -> new IllegalArgumentException("operations.error.service.notFound"));
            workOrder.addTask(command.serviceId(), command.mechanicId(), command.description(), service.getPrice());
            WorkOrder savedWorkOrder = workOrderRepository.save(workOrder);
            return Result.success(savedWorkOrder);

        } catch (IllegalArgumentException e) {
            return Result.failure(new WorkOrderCommandFailure.NotFound(e.getMessage()));
        } catch (IllegalStateException e) {
            return Result.failure(new WorkOrderCommandFailure.InvalidState(e.getMessage()));
        }
    }

    /**
     * Handles the AddProductToTaskCommand by loading the specified Work Order aggregate, invoking the addProductToTask method with the provided product details, and saving the updated aggregate back to the repository. The method returns a Result object that encapsulates either the updated Work Order on success or a WorkOrderCommandFailure on failure, with specific handling for not found and invalid state scenarios.
     * @param command The command object containing the Work Order ID, Task ID, and the details of the Product to be added (product ID, quantity).
     * @return A Result object containing either the updated Work Order on success or a WorkOrderCommandFailure on failure, with appropriate error handling for not found and invalid state scenarios.
     */
    @Override
    @Transactional
    public Result<WorkOrder, WorkOrderCommandFailure> handle(AddProductToTaskCommand command) {
        try {
            WorkOrder workOrder = findWorkOrderOrThrow(command.workOrderId());
            var product = productQueryGateway.findProductById(command.productId().value())
                    .orElseThrow(() -> new IllegalArgumentException("operations.error.product.notFound"));
            workOrder.addProductToTask(command.taskId(), command.productId(), command.quantity(), product.currentSellingPrice());
            WorkOrder savedWorkOrder = workOrderRepository.save(workOrder);
            return Result.success(savedWorkOrder);

        } catch (IllegalArgumentException e) {
            return Result.failure(new WorkOrderCommandFailure.NotFound(e.getMessage()));
        } catch (IllegalStateException e) {
            return Result.failure(new WorkOrderCommandFailure.InvalidState(e.getMessage()));
        }
    }

    /**
     * Handles the RemoveProductFromTaskCommand by loading the specified Work Order aggregate, invoking the removeProductFromTask method with the provided identifiers, and saving the updated aggregate back to the repository. The method returns a Result object that encapsulates either the updated Work Order on success or a WorkOrderCommandFailure on failure, with specific handling for not found and invalid state scenarios.
     * @param command The command object containing the Work Order ID, Task ID, and the Product ID of the product to be removed.
     * @return A Result object containing either the updated Work Order on success or a WorkOrderCommandFailure on failure, with appropriate error handling for not found and invalid state scenarios.
     */
    @Override
    @Transactional
    public Result<WorkOrder, WorkOrderCommandFailure> handle(RemoveProductFromTaskCommand command) {
        try {
            WorkOrder workOrder = findWorkOrderOrThrow(command.workOrderId());

            workOrder.removeProductFromTask(command.taskId(), command.productId());
            WorkOrder savedWorkOrder = workOrderRepository.save(workOrder);
            return Result.success(savedWorkOrder);

        } catch (IllegalArgumentException e) {
            return Result.failure(new WorkOrderCommandFailure.NotFound(e.getMessage()));
        } catch (IllegalStateException e) {
            return Result.failure(new WorkOrderCommandFailure.InvalidState(e.getMessage()));
        }
    }

    /**
     * Handles the RemoveTaskFromWorkOrderCommand by loading the specified Work Order aggregate, invoking the removeTask method with the provided Task ID, and saving the updated aggregate back to the repository. The method returns a Result object that encapsulates either the updated Work Order on success or a WorkOrderCommandFailure on failure, with specific handling for not found and invalid state scenarios.
     * @param command The command object containing the Work Order ID and Task ID of the task to be removed.
     * @return A Result object containing either the updated Work Order on success or a WorkOrderCommandFailure on failure, with appropriate error handling for not found and invalid state scenarios.
     */
    @Override
    @Transactional
    public Result<WorkOrder, WorkOrderCommandFailure> handle(RemoveTaskFromWorkOrderCommand command) {
        try {
            WorkOrder workOrder = findWorkOrderOrThrow(command.workOrderId());
            workOrder.removeTask(command.taskId());
            WorkOrder savedWorkOrder = workOrderRepository.save(workOrder);
            return Result.success(savedWorkOrder);

        } catch (IllegalArgumentException e) {
            return Result.failure(new WorkOrderCommandFailure.NotFound(e.getMessage()));
        } catch (IllegalStateException e) {
            return Result.failure(new WorkOrderCommandFailure.InvalidState(e.getMessage()));
        }
    }

    /**
     * Handles the StartTaskCommand by loading the specified Work Order aggregate, invoking the startTask method with the provided Task ID, and saving the updated aggregate back to the repository. The method returns a Result object that encapsulates either the updated Work Order on success or a WorkOrderCommandFailure on failure, with specific handling for not found and invalid state scenarios.
     * @param command The command object containing the Work Order ID and Task ID of the task to be started.
     * @return A Result object containing either the updated Work Order on success or a WorkOrderCommandFailure on failure, with appropriate error handling for not found and invalid state scenarios.
     */
    @Override
    @Transactional
    public Result<WorkOrder, WorkOrderCommandFailure> handle(StartTaskCommand command) {
        try {
            WorkOrder workOrder = findWorkOrderOrThrow(command.workOrderId());
            workOrder.startTask(command.taskId());
            WorkOrder savedWorkOrder = workOrderRepository.save(workOrder);
            return Result.success(savedWorkOrder);

        } catch (IllegalArgumentException e) {
            return Result.failure(new WorkOrderCommandFailure.NotFound(e.getMessage()));
        } catch (IllegalStateException e) {
            return Result.failure(new WorkOrderCommandFailure.InvalidState(e.getMessage()));
        }
    }

    /**
     * Handles the CompleteTaskCommand by loading the specified Work Order aggregate, invoking the completeTask method with the provided Task ID, and saving the updated aggregate back to the repository. The method returns a Result object that encapsulates either the updated Work Order on success or a WorkOrderCommandFailure on failure, with specific handling for not found and invalid state scenarios.
     * @param command The command object containing the Work Order ID and Task ID of the task to be completed.
     * @return A Result object containing either the updated Work Order on success or a WorkOrderCommandFailure on failure, with appropriate error handling for not found and invalid state scenarios.
     */
    @Override
    @Transactional
    public Result<WorkOrder, WorkOrderCommandFailure> handle(CompleteTaskCommand command) {
        try {
            WorkOrder workOrder = findWorkOrderOrThrow(command.workOrderId());
            workOrder.completeTask(command.taskId());
            WorkOrder savedWorkOrder = workOrderRepository.save(workOrder);
            return Result.success(savedWorkOrder);

        } catch (IllegalArgumentException e) {
            return Result.failure(new WorkOrderCommandFailure.NotFound(e.getMessage()));
        } catch (IllegalStateException e) {
            return Result.failure(new WorkOrderCommandFailure.InvalidState(e.getMessage()));
        }
    }

    /**
     * Handles the ReopenTaskCommand by loading the specified Work Order aggregate, invoking the reopenTask method with the provided Task ID, and saving the updated aggregate back to the repository. The method returns a Result object that encapsulates either the updated Work Order on success or a WorkOrderCommandFailure on failure, with specific handling for not found and invalid state scenarios.
     * @param command The command object containing the Work Order ID and Task ID of the task to be reopened.
     * @return A Result object containing either the updated Work Order on success or a WorkOrderCommandFailure on failure, with appropriate error handling for not found and invalid state scenarios.
     */
    @Override
    @Transactional
    public Result<WorkOrder, WorkOrderCommandFailure> handle(ReopenTaskCommand command) {
        try {
            WorkOrder workOrder = findWorkOrderOrThrow(command.workOrderId());

            workOrder.reopenTask(command.taskId());
            WorkOrder savedWorkOrder = workOrderRepository.save(workOrder);
            return Result.success(savedWorkOrder);

        } catch (IllegalArgumentException e) {
            return Result.failure(new WorkOrderCommandFailure.NotFound(e.getMessage()));
        } catch (IllegalStateException e) {
            return Result.failure(new WorkOrderCommandFailure.InvalidState(e.getMessage()));
        }
    }

    /**
     * Handles the MarkWorkOrderAsPaidCommand by loading the specified Work Order aggregate, invoking the markAsPaid method to transition the work order to a paid state (which also triggers the physical inventory deduction), and saving the updated aggregate back to the repository. The method returns a Result object that encapsulates either the updated Work Order on success or a WorkOrderCommandFailure on failure, with specific handling for not found and invalid state scenarios.
     * @param command The command object containing the Work Order ID of the work order to be marked as paid.
     * @return A Result object containing either the updated Work Order on success or a WorkOrderCommandFailure on failure, with appropriate error handling for not found and invalid state scenarios.
     */
    @Override
    @Transactional
    public Result<WorkOrder, WorkOrderCommandFailure> handle(MarkWorkOrderAsPaidCommand command) {
        try {
            WorkOrder workOrder = findWorkOrderOrThrow(command.workOrderId());
            workOrder.markAsPaid();
            WorkOrder savedWorkOrder = workOrderRepository.save(workOrder);
            return Result.success(savedWorkOrder);

        } catch (IllegalArgumentException e) {
            return Result.failure(new WorkOrderCommandFailure.NotFound(e.getMessage()));
        } catch (IllegalStateException e) {
            return Result.failure(new WorkOrderCommandFailure.InvalidState(e.getMessage()));
        }
    }

    /**
     * Handles the UpdateWorkOrderTaskDetailsCommand by loading the specified Work Order aggregate, invoking the updateTaskDetails method with the provided Task ID and new details, and saving the updated aggregate back to the repository. The method returns a Result object that encapsulates either the updated Work Order on success or a WorkOrderCommandFailure on failure, with specific handling for not found and invalid state scenarios.
     * @param command The command object containing the Work Order ID, Task ID, and the new details for the task (service ID, mechanic ID, description, labor price) to be updated.
     * @return A Result object containing either the updated Work Order on success or a WorkOrderCommandFailure on failure, with appropriate error handling for not found and invalid state scenarios.
     */
    @Override
    @Transactional
    public Result<WorkOrder, WorkOrderCommandFailure> handle(UpdateWorkOrderTaskDetailsCommand command) {
        try {
            WorkOrder workOrder = findWorkOrderOrThrow(command.workOrderId());
            var service = serviceRepository.findById(command.serviceId())
                    .orElseThrow(() -> new IllegalArgumentException("operations.error.service.notFound"));
            workOrder.updateTaskDetails(
                    command.taskId(),
                    command.serviceId(),
                    command.mechanicId(),
                    command.description(),
                    service.getPrice()
            );
            WorkOrder savedWorkOrder = workOrderRepository.save(workOrder);
            return Result.success(savedWorkOrder);
        } catch (IllegalArgumentException e) {
            return Result.failure(new WorkOrderCommandFailure.NotFound(e.getMessage()));
        } catch (IllegalStateException e) {
            return Result.failure(new WorkOrderCommandFailure.InvalidState(e.getMessage()));
        }
    }

    /**
     * Handles the UpdateProductQuantityInTaskCommand by loading the specified Work Order aggregate, invoking the updateProductQuantityInTask method with the provided Task ID, Product ID, and new quantity, and saving the updated aggregate back to the repository. The method returns a Result object that encapsulates either the updated Work Order on success or a WorkOrderCommandFailure on failure, with specific handling for not found and invalid state scenarios.
     * @param command The command object containing the Work Order ID, Task ID, Product ID, and the new quantity for the product to be updated.
     * @return A Result object containing either the updated Work Order on success or a WorkOrderCommandFailure on failure, with appropriate error handling for not found and invalid state scenarios.
     */
    @Override
    @Transactional
    public Result<WorkOrder, WorkOrderCommandFailure> handle(UpdateProductQuantityInTaskCommand command) {
        try {
            WorkOrder workOrder = findWorkOrderOrThrow(command.workOrderId());
            workOrder.updateProductQuantityInTask(
                    command.taskId(),
                    command.productId(),
                    command.newQuantity()
            );
            WorkOrder savedWorkOrder = workOrderRepository.save(workOrder);
            return Result.success(savedWorkOrder);
        } catch (IllegalArgumentException e) {
            return Result.failure(new WorkOrderCommandFailure.NotFound(e.getMessage()));
        } catch (IllegalStateException e) {
            return Result.failure(new WorkOrderCommandFailure.InvalidState(e.getMessage()));
        }
    }

    /**
     * Handles the DeleteWorkOrderCommand by loading the specified Work Order aggregate, invoking the delete method to mark the work order as deleted (soft delete), and saving the updated aggregate back to the repository. The method returns a Result object that encapsulates either the updated Work Order on success or a WorkOrderCommandFailure on failure, with specific handling for not found and invalid state scenarios.
     * @param command The command object containing the Work Order ID of the work order to be deleted.
     * @return A Result object containing either the updated Work Order on success or a WorkOrderCommandFailure on failure, with appropriate error handling for not found and invalid state scenarios.
     */
    @Override
    @Transactional
    public Result<WorkOrder, WorkOrderCommandFailure> handle(DeleteWorkOrderCommand command) {
        try {
            WorkOrder workOrder = findWorkOrderOrThrow(command.workOrderId());
            workOrder.delete();
            WorkOrder savedWorkOrder = workOrderRepository.save(workOrder);
            return Result.success(savedWorkOrder);
        } catch (IllegalArgumentException e) {
            return Result.failure(new WorkOrderCommandFailure.NotFound(e.getMessage()));
        } catch (IllegalStateException e) {
            return Result.failure(new WorkOrderCommandFailure.InvalidState(e.getMessage()));
        }
    }

    /**
     * Utility method to find a Work Order by its ID or throw an IllegalArgumentException if not found. This method is used across multiple command handlers to ensure consistent error handling when a Work Order cannot be found in the repository.
     * @param workOrderId The WorkOrderId of the Work Order to be retrieved.
     * @return The Work Order aggregate if found.
     * @throws IllegalArgumentException if the Work Order with the specified ID is not found in the repository, with a message indicating that the work order was not found.
     */
    private WorkOrder findWorkOrderOrThrow(WorkOrderId workOrderId) {
        return workOrderRepository.findById(workOrderId)
                .orElseThrow(() -> new IllegalArgumentException("operations.error.workOrder.notFound"));
    }
}