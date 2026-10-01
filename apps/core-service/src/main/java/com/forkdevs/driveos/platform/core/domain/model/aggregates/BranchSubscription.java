package com.forkdevs.driveos.platform.core.domain.model.aggregates;

import com.forkdevs.driveos.platform.core.domain.model.valueobjects.BillingCycle;
import com.forkdevs.driveos.platform.core.domain.model.valueobjects.BranchSubscriptionId;
import com.forkdevs.driveos.platform.core.domain.model.valueobjects.SubscriptionPlanId;
import com.forkdevs.driveos.platform.core.domain.model.valueobjects.SubscriptionStatus;
import com.forkdevs.driveos.platform.shared.domain.model.valueobjects.BranchId;

import lombok.Getter;
import java.util.Date;

@Getter
public class BranchSubscription {

    private BranchSubscriptionId id;
    private BranchId branchId;
    private SubscriptionPlanId planId;
    private SubscriptionStatus status;
    private BillingCycle billingCycle;
    private Date startDate;
    private Date endDate;
    private Date canceledAt;

    public BranchSubscription() {}

    public BranchSubscription(BranchId branchId, SubscriptionPlanId planId, BillingCycle billingCycle, Date startDate, Date endDate) {
        if (billingCycle == null) throw new IllegalArgumentException("core.error.billingCycle.required");
        if (startDate == null) throw new IllegalArgumentException("core.error.startDate.required");
        if (endDate == null) throw new IllegalArgumentException("core.error.endDate.required");

        this.branchId = branchId;
        this.planId = planId;
        this.status = SubscriptionStatus.ACTIVE;
        this.billingCycle = billingCycle;
        this.startDate = startDate;
        this.endDate = endDate;
    }

    public BranchSubscription(BranchSubscriptionId id, BranchId branchId, SubscriptionPlanId planId, SubscriptionStatus status, BillingCycle billingCycle, Date startDate, Date endDate, Date canceledAt) {
        this(branchId, planId, billingCycle, startDate, endDate);
        this.id = id;
        this.status = status;
        this.canceledAt = canceledAt;
    }

    public void cancel(Date canceledAt) {
        this.status = SubscriptionStatus.CANCELED;
        this.canceledAt = canceledAt;
    }
}

