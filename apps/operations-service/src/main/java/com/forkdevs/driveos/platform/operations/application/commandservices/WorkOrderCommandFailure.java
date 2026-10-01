package com.forkdevs.driveos.platform.operations.application.commandservices;

/**
 * Interface representing possible failure outcomes of Work Order command operations. This sealed interface defines specific failure cases that can occur during the execution of commands related to Work Orders, such as NotFound, InvalidState, and Duplicate. Each failure case includes a message describing the reason for the failure.
 * @author Joel Huamani Estefanero
 */
public sealed interface WorkOrderCommandFailure permits
        WorkOrderCommandFailure.NotFound,
        WorkOrderCommandFailure.InvalidState,
        WorkOrderCommandFailure.Duplicate {
    record NotFound(String message) implements WorkOrderCommandFailure {}

    record InvalidState(String message) implements WorkOrderCommandFailure {}

    record Duplicate(String message) implements WorkOrderCommandFailure {}
}
