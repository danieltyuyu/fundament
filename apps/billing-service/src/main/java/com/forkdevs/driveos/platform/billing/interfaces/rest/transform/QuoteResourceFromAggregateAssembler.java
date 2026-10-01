package com.forkdevs.driveos.platform.billing.interfaces.rest.transform;

import com.forkdevs.driveos.platform.billing.domain.model.aggregates.Quote;
import com.forkdevs.driveos.platform.billing.interfaces.rest.resources.QuoteResource;

public class QuoteResourceFromAggregateAssembler {
    public static QuoteResource toResourceFromAggregate(Quote aggregate) {
        return new QuoteResource(
                aggregate.getId(),
                aggregate.getWorkOrderId(),
                aggregate.getBranchId().value(),
                aggregate.getSubtotalAmount().amount(),
                aggregate.getDiscountPercentage(),
                aggregate.getTotalAmount().amount(),
                aggregate.getStatus().name()
        );
    }
}
