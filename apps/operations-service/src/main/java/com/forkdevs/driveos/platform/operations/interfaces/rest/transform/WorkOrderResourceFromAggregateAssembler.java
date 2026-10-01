package com.forkdevs.driveos.platform.operations.interfaces.rest.transform;

import com.forkdevs.driveos.platform.operations.domain.model.aggregates.WorkOrder;
import com.forkdevs.driveos.platform.operations.domain.model.entities.WorkOrderTask;
import com.forkdevs.driveos.platform.operations.domain.model.entities.WorkOrderTaskProduct;
import com.forkdevs.driveos.platform.operations.interfaces.rest.resources.*;

import java.util.ArrayList;
import java.util.List;

/**
 * Assembler class to translate Work Order domain aggregates/entities to REST resources.
 * @author Joel Huamani Estefanero
 */
public final class WorkOrderResourceFromAggregateAssembler {

    private WorkOrderResourceFromAggregateAssembler() {}

    /**
     * Transform a WorkOrder aggregate into a WorkOrderResource, including its associated tasks and products. This method iterates through the tasks and products of the WorkOrder, converting each to their respective REST resources while filtering out any that are marked as deleted.
     * @param workOrder The WorkOrder aggregate to be transformed into a REST resource. This aggregate contains all the necessary information about the work order, including its tasks and products, which will be included in the resulting WorkOrderResource.
     * @return A WorkOrderResource that represents the given WorkOrder aggregate, including its tasks and products. This resource is suitable for use in REST API responses, providing a structured representation of the work order data that can be easily consumed by clients.
     */
    public static WorkOrderResource toResourceFromAggregate(WorkOrder workOrder, String branchCode) {
        List<WorkOrderTaskResource> taskResources = new ArrayList<>();

        for (WorkOrderTask task : workOrder.getTasks()) {
            if (!task.isDeleted()) {
                taskResources.add(toResourceFromTask(task));
            }
        }

        String formattedNumber = String.format("%s-%06d", branchCode, workOrder.getInternalNumber());

        return new WorkOrderResource(
                workOrder.getId().value(),
                workOrder.getAppointmentId().value(),
                workOrder.getBranchId().value(),
                workOrder.getVehicleId().value(),
                workOrder.getCustomerId().value(),
                workOrder.getInternalNumber(),
                formattedNumber,
                workOrder.getStatus().name(),
                workOrder.getDiagnosticSummary().value(),
                workOrder.getMileageIn().value(),
                workOrder.getTotalAmount().amount(),
                taskResources,
                workOrder.getCreatedAt(),
                workOrder.getUpdatedAt()
        );
    }

    /**
     * Transform a WorkOrderTask entity into a WorkOrderTaskResource, including its associated products. This method iterates through the products of the WorkOrderTask, converting each to its respective REST resource while filtering out any that are marked as deleted.
     * @param task The WorkOrderTask entity to be transformed into a WorkOrderTaskResource. This entity contains all the necessary information about the task, including its products, which will be included in the resulting WorkOrderTaskResource.
     * @return A WorkOrderTaskResource that represents the given WorkOrderTask entity, including its products. This resource is suitable for use in REST API responses, providing a structured representation of the task data that can be easily consumed by clients.
     */
    private static WorkOrderTaskResource toResourceFromTask(WorkOrderTask task) {
        List<WorkOrderTaskProductResource> productResources = new ArrayList<>();

        for (WorkOrderTaskProduct product : task.getProducts()) {
            if (!product.isDeleted()) {
                productResources.add(toResourceFromProduct(product));
            }
        }

        return new WorkOrderTaskResource(
                task.getId().value(),
                task.getServiceId().value(),
                task.getBranchId().value(),
                task.getAssignedMechanicId().value(),
                task.getStatus().name(),
                task.getDescription().value(),
                task.getPrice().amount(),
                task.getStartedAt(),
                task.getCompletedAt(),
                productResources,
                task.getCreatedAt()
        );
    }

    /**
     * Transform a WorkOrderTaskProduct entity into a WorkOrderTaskProductResource. This method extracts the relevant information from the WorkOrderTaskProduct entity and constructs a corresponding REST resource that can be used in API responses.
     * @param product The WorkOrderTaskProduct entity to be transformed into a WorkOrderTaskProductResource. This entity contains all the necessary information about the product, such as its ID, associated product and branch IDs, quantity, unit price, total amount, and creation timestamp, which will be included in the resulting WorkOrderTaskProductResource.
     * @return A WorkOrderTaskProductResource that represents the given WorkOrderTaskProduct entity. This resource is suitable for use in REST API responses, providing a structured representation of the product data that can be easily consumed by clients.
     */
    private static WorkOrderTaskProductResource toResourceFromProduct(WorkOrderTaskProduct product) {
        return new WorkOrderTaskProductResource(
                product.getId().value(),
                product.getProductId().value(),
                product.getBranchId().value(),
                product.getQuantity().value(),
                product.getUnitPrice().amount(),
                product.getTotalAmount().amount(),
                product.getCreatedAt()
        );
    }
}