package com.forkdevs.driveos.platform.operations.domain.model.commands;

import com.forkdevs.driveos.platform.operations.domain.model.valueobjects.*;
import com.forkdevs.driveos.platform.shared.domain.model.valueobjects.*;

/**
 * Command object representing the data required to create a new Work Order in the system. This command encapsulates all necessary information such as appointment details, branch, vehicle, customer, and diagnostic information.
 * @param appointmentId
 * @param branchId
 * @param vehicleId
 * @param customerId
 * @param diagnosticSummary
 * @param mileageIn
 * @author Joel Huamani Estefanero
 */
public record CreateWorkOrderCommand(
        AppointmentId appointmentId,
        BranchId branchId,
        VehicleId vehicleId,
        CustomerId customerId,
        DiagnosticSummary diagnosticSummary,
        Mileage mileageIn
) {}
