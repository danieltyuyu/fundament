package com.forkdevs.driveos.platform.shared.domain.model.events;

import java.util.UUID;
/**
 * Event representing the successful processing of a payment for a work order. This event is published after the payment has been processed and can be used to trigger subsequent actions, such as updating the work order status or notifying relevant parties.
 * @record PaymentProcessedEvent
 * @param workOrderId The unique identifier of the work order for which the payment was processed
 * @author Joel Huamani Estefanero
 */
public record PaymentProcessedEvent(UUID workOrderId) {}
