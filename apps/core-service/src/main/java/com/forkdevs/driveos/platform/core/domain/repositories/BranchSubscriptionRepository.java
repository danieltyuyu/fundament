package com.forkdevs.driveos.platform.core.domain.repositories;

import com.forkdevs.driveos.platform.core.domain.model.aggregates.BranchSubscription;
import com.forkdevs.driveos.platform.shared.domain.model.valueobjects.BranchId;
import com.forkdevs.driveos.platform.core.domain.model.valueobjects.BranchSubscriptionId;

import java.util.List;
import java.util.Optional;

public interface BranchSubscriptionRepository {
    BranchSubscription save(BranchSubscription branchSubscription);
    Optional<BranchSubscription> findById(BranchSubscriptionId id);
    List<BranchSubscription> findAllByBranchId(BranchId branchId);
    Optional<BranchSubscription> findActiveByBranchId(BranchId branchId);
}

