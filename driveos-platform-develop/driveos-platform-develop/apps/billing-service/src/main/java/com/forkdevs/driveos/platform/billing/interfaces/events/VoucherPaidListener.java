package com.forkdevs.driveos.platform.billing.interfaces.events;

import com.forkdevs.driveos.platform.billing.domain.model.events.VoucherPaidEvent;
import com.forkdevs.driveos.platform.billing.domain.repositories.QuoteRepository;
import com.forkdevs.driveos.platform.shared.domain.model.events.PaymentProcessedEvent;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

/**
 * Listener that captures internal domain events from the Billing module and
 * translates them into Integration Events for the rest of the system.
 */
@Component
public class VoucherPaidListener {

    private final QuoteRepository quoteRepository;
    private final ApplicationEventPublisher eventPublisher;

    public VoucherPaidListener(QuoteRepository quoteRepository, ApplicationEventPublisher eventPublisher) {
        this.quoteRepository = quoteRepository;
        this.eventPublisher = eventPublisher;
    }

    /**
     * Listens for VoucherPaidEvent (Domain Event), retrieves the associated Quote 
     * to obtain the workOrderId, and publishes a PaymentProcessedEvent (Integration Event).
     */
    @EventListener
    public void onVoucherPaid(VoucherPaidEvent event) {
        var quoteOpt = quoteRepository.findById(event.quoteId());
        
        quoteOpt.ifPresent(quote -> {
            // Publish the Integration Event for other modules (like operations) to react
            eventPublisher.publishEvent(new PaymentProcessedEvent(quote.getWorkOrderId()));
        });
    }
}
