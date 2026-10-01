package com.forkdevs.driveos.platform.operations.interfaces.events;

import com.forkdevs.driveos.platform.operations.application.commandservices.WorkOrderCommandService;
import com.forkdevs.driveos.platform.operations.domain.model.commands.MarkWorkOrderAsPaidCommand;
import com.forkdevs.driveos.platform.operations.domain.model.valueobjects.WorkOrderId;
import com.forkdevs.driveos.platform.shared.domain.model.events.PaymentProcessedEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

/**
 * Listener for handling events related to Work Order payments. This component listens for PaymentProcessedEvent events, which are emitted by the Billing module when a payment is successfully processed. Upon receiving such an event, this listener automatically executes the necessary command to mark the corresponding Work Order as paid in the operations domain.
 * @author Joel Huamani Estefanero
 */
@Component
public class WorkOrderPaymentListener {
    private final WorkOrderCommandService commandService;

    /**
     * Constructor for WorkOrderPaymentListener, which injects the WorkOrderCommandService to allow the listener to execute commands in response to events.
     * @param commandService The command service used to handle commands related to Work Orders, such as marking a Work Order as paid when a payment is processed.
     */
    public WorkOrderPaymentListener(WorkOrderCommandService commandService) {
        this.commandService = commandService;
    }

    /**
     * Event listener method that is triggered when a PaymentProcessedEvent is emitted. This method extracts the Work Order ID from the event and executes the MarkWorkOrderAsPaidCommand to update the status of the Work Order to paid.
     * @param event The PaymentProcessedEvent containing information about the processed payment, including the Work Order ID that should be marked as paid.
     */
    @EventListener
    public void onPaymentProcessed(PaymentProcessedEvent event) {
        commandService.handle(new MarkWorkOrderAsPaidCommand(new WorkOrderId(event.workOrderId())));
    }
}