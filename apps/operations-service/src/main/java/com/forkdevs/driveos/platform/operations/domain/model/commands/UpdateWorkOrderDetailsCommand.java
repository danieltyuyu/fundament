package com.forkdevs.driveos.platform.operations.domain.model.commands;

import com.forkdevs.driveos.platform.operations.domain.model.valueobjects.DiagnosticSummary;
import com.forkdevs.driveos.platform.operations.domain.model.valueobjects.WorkOrderId;
import com.forkdevs.driveos.platform.shared.domain.model.valueobjects.Mileage;

/**
 * Command representing the intent to update the details of a Work Order, specifically the diagnostic summary and mileage.
 * This command encapsulates the necessary information to perform the update action on a Work Order within the
 * @param workOrderId
 * @param diagnosticSummary
 * @param mileageIn
 * @author Joel Huamani Estefanero
 */
public record UpdateWorkOrderDetailsCommand(
        WorkOrderId workOrderId,
        DiagnosticSummary diagnosticSummary,
        Mileage mileageIn
) {}
