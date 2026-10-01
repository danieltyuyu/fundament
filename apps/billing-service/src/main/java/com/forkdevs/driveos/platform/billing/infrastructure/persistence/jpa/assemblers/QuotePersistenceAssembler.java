package com.forkdevs.driveos.platform.billing.infrastructure.persistence.jpa.assemblers;

import com.forkdevs.driveos.platform.billing.domain.model.aggregates.Quote;
import com.forkdevs.driveos.platform.billing.infrastructure.persistence.jpa.entities.QuotePersistenceEntity;

public class QuotePersistenceAssembler {

    public static QuotePersistenceEntity toEntity(Quote aggregate) {
        if (aggregate == null) return null;
        var entity = new QuotePersistenceEntity();
        updateEntityFromAggregate(entity, aggregate);
        return entity;
    }

    public static void updateEntityFromAggregate(QuotePersistenceEntity entity, Quote aggregate) {
        if (aggregate == null || entity == null) return;
        entity.setWorkOrderId(aggregate.getWorkOrderId());
        entity.setBranchId(aggregate.getBranchId());
        entity.setSubtotalAmount(aggregate.getSubtotalAmount());
        entity.setDiscountPercentage(aggregate.getDiscountPercentage());
        entity.setTotalAmount(aggregate.getTotalAmount());
        entity.setStatus(aggregate.getStatus());
    }

    public static Quote toAggregate(QuotePersistenceEntity entity) {
        if (entity == null) return null;
        return new Quote(
                entity.getId(),
                entity.getWorkOrderId(),
                entity.getBranchId(),
                entity.getSubtotalAmount(),
                entity.getDiscountPercentage(),
                entity.getTotalAmount(),
                entity.getStatus()
        );
    }
}
