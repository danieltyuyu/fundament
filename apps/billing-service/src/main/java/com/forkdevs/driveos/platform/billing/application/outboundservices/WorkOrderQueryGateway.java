package com.forkdevs.driveos.platform.billing.application.outboundservices;

import com.forkdevs.driveos.platform.shared.domain.model.valueobjects.Money;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface WorkOrderQueryGateway {
    record WorkOrderSummary(UUID workOrderId, Money totalAmount, List<WorkOrderItemSummary> items) {}
    record WorkOrderItemSummary(String description, int quantity, Money unitPrice) {}

    Optional<WorkOrderSummary> getWorkOrderSummary(UUID workOrderId);
}
