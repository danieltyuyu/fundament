package com.forkdevs.driveos.platform.operations.interfaces.rest.transform;

import com.forkdevs.driveos.platform.operations.domain.model.commands.*;
import com.forkdevs.driveos.platform.operations.domain.model.valueobjects.*;
import com.forkdevs.driveos.platform.operations.interfaces.rest.resources.*;
import com.forkdevs.driveos.platform.shared.domain.model.valueobjects.*;

import java.util.UUID;

/**
 * Assembler class to map incoming HTTP resources into domain-level Commands.
 * @author Joel Huamani Estefanero
 */
public final class WorkOrderCommandFromResourceAssembler {

    private WorkOrderCommandFromResourceAssembler() {}

    /**
     * Transforms the CreateWorkOrderResource into a CreateWorkOrderCommand, mapping all necessary fields and converting them into the appropriate value objects required by the domain model.
     * @param resource The incoming resource containing the data for creating a new Work Order, typically received from an HTTP request body in a REST API endpoint.
     * @return A CreateWorkOrderCommand instance populated with the data from the resource, ready to be processed by the application layer to create a new Work Order in the system.
     */
    public static CreateWorkOrderCommand toCommandFromResource(CreateWorkOrderResource resource) {
        return new CreateWorkOrderCommand(
                new AppointmentId(resource.appointmentId()),
                new BranchId(resource.branchId()),
                new VehicleId(resource.vehicleId()),
                new CustomerId(resource.customerId()),
                new DiagnosticSummary(resource.diagnosticSummary()),
                new Mileage(resource.mileageIn())
        );
    }

    /**
     * Transforms the UpdateWorkOrderDetailsResource into an UpdateWorkOrderDetailsCommand, mapping the diagnostic summary and mileage fields to their corresponding value objects for updating the details of an existing Work Order.
     * @param workOrderId The unique identifier of the Work Order to be updated, typically extracted from the URL path variable in a REST API endpoint.
     * @param resource The incoming resource containing the new diagnostic summary and mileage information for the Work Order, received from the HTTP request body.
     * @return An UpdateWorkOrderDetailsCommand instance populated with the work order ID and the new details from the resource, ready to be processed by the application layer to update the Work Order in the system.
     */
    public static UpdateWorkOrderDetailsCommand toCommandFromResource(UUID workOrderId, UpdateWorkOrderDetailsResource resource) {
        return new UpdateWorkOrderDetailsCommand(
                new WorkOrderId(workOrderId),
                new DiagnosticSummary(resource.diagnosticSummary()),
                new Mileage(resource.mileageIn())
        );
    }

    /**
     * Transforms the AddTaskResource into an AddTaskToWorkOrderCommand.
     * @param workOrderId The unique identifier of the Work Order.
     * @param resource The incoming resource containing the task details.
     * @return An AddTaskToWorkOrderCommand instance.
     */
    public static AddTaskToWorkOrderCommand toCommandFromResource(UUID workOrderId, AddTaskResource resource) {
        return new AddTaskToWorkOrderCommand(
                new WorkOrderId(workOrderId),
                new ServiceId(resource.serviceId()),
                new MechanicId(resource.assignedMechanicId()),
                new TaskDescription(resource.description())
        );
    }

    /**
     * Transforms the AddProductResource into an AddProductToTaskCommand, mapping the product ID, quantity, and unit price fields to their corresponding value objects for adding a Product to a Task within a Work Order.
     * @param workOrderId The unique identifier of the Work Order to which the Task belongs, typically extracted from the URL path variable in a REST API endpoint.
     * @param taskId The unique identifier of the Task to which the Product will be added, also typically extracted from the URL path variable in a REST API endpoint.
     * @param resource The incoming resource containing the product ID, quantity, and unit price information for the Product to be added to the Task, received from the HTTP request body.
     * @return An AddProductToTaskCommand instance populated with the work order ID, task ID, and the product details from the resource, ready to be processed by the application layer to add the Product to the Task in the system.
     */
    public static AddProductToTaskCommand toCommandFromResource(UUID workOrderId, UUID taskId, AddProductResource resource) {
        return new AddProductToTaskCommand(
                new WorkOrderId(workOrderId),
                new WorkOrderTaskId(taskId),
                new ProductId(resource.productId()),
                new Quantity(resource.quantity())
        );
    }

    /**
     * Transforms the UpdateWorkOrderTaskDetailsResource into an UpdateWorkOrderTaskDetailsCommand, mapping the service ID, mechanic ID, description, and labor price fields to their corresponding value objects for updating the details of a Task within a Work Order.
     * @param workOrderId The unique identifier of the Work Order to which the Task belongs, typically extracted from the URL path variable in a REST API endpoint.
     * @param taskId The unique identifier of the Task to be updated, also typically extracted from the URL path variable in a REST API endpoint.
     * @param resource The incoming resource containing the new service ID, mechanic ID, description, and labor price information for the Task to be updated, received from the HTTP request body.
     * @return An UpdateWorkOrderTaskDetailsCommand instance populated with the work order ID, task ID, and the new task details from the resource, ready to be processed by the application layer to update the Task in the system.
     */
    public static UpdateWorkOrderTaskDetailsCommand toCommandFromResource(UUID workOrderId, UUID taskId, UpdateWorkOrderTaskDetailsResource resource) {
        return new UpdateWorkOrderTaskDetailsCommand(
                new WorkOrderId(workOrderId),
                new WorkOrderTaskId(taskId),
                new ServiceId(resource.serviceId()),
                new MechanicId(resource.assignedMechanicId()),
                new TaskDescription(resource.description())
        );
    }

    /**
     * Transforms the UpdateProductQuantityInTaskResource into an UpdateProductQuantityInTaskCommand, mapping the product ID and quantity fields to their corresponding value objects for updating the quantity of a Product in a Task within a Work Order.
     * @param workOrderId The unique identifier of the Work Order to which the Task belongs, typically extracted from the URL path variable in a REST API endpoint.
     * @param taskId The unique identifier of the Task to which the Product belongs, also typically extracted from the URL path variable in a REST API endpoint.
     * @param productId The unique identifier of the Product whose quantity is to be updated, also typically extracted from the URL path variable in a REST API endpoint.
     * @param resource The incoming resource containing the new quantity information for the Product to be updated in the Task, received from the HTTP request body.
     * @return An UpdateProductQuantityInTaskCommand instance populated with the work order ID, task ID, product ID, and the new quantity from the resource, ready to be processed by the application layer to update the Product quantity in the Task in the system.
     */
    public static UpdateProductQuantityInTaskCommand toCommandFromResource(UUID workOrderId, UUID taskId, UUID productId, UpdateProductQuantityInTaskResource resource) {
        return new UpdateProductQuantityInTaskCommand(
                new WorkOrderId(workOrderId),
                new WorkOrderTaskId(taskId),
                new ProductId(productId),
                new Quantity(resource.quantity())
        );
    }
}