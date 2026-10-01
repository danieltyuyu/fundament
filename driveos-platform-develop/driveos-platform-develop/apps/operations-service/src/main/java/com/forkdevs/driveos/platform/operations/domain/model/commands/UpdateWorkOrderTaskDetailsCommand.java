package com.forkdevs.driveos.platform.operations.domain.model.commands;

import com.forkdevs.driveos.platform.operations.domain.model.valueobjects.*;

/**
 * Command representing the intent to update the details of a Task within a Work Order.
 * This command encapsulates all necessary information to perform the update action, including identifiers for the work order and task, as well as the new details to be applied.
 * @param workOrderId
 * @param taskId
 * @param serviceId
 * @param mechanicId
 * @param description
 * @author Joel Huamani Estefanero
 */
public record UpdateWorkOrderTaskDetailsCommand(
        WorkOrderId workOrderId,
        WorkOrderTaskId taskId,
        ServiceId serviceId,
        MechanicId mechanicId,
        TaskDescription description
) {}
