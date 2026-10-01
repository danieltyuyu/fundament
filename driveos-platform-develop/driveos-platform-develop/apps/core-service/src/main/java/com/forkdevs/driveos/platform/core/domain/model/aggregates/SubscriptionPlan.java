package com.forkdevs.driveos.platform.core.domain.model.aggregates;

import com.forkdevs.driveos.platform.core.domain.model.valueobjects.SubscriptionPlanId;

import lombok.Getter;

import java.util.UUID;

@Getter
public class SubscriptionPlan {

    private SubscriptionPlanId id;
    private String name;
    private double monthlyPrice;
    private int maxObd2Devices;
    private int maxMonthlySnapshotsPerVehicle;
    private int maxCustomers;
    private int maxStaffAccounts;
    private boolean isActive;

    public SubscriptionPlan() {
        this.isActive = true;
    }

    public SubscriptionPlan(SubscriptionPlanId id, String name, double monthlyPrice, int maxObd2Devices, int maxMonthlySnapshotsPerVehicle, int maxCustomers, int maxStaffAccounts, boolean isActive) {
        this(name, monthlyPrice, maxObd2Devices, maxMonthlySnapshotsPerVehicle, maxCustomers, maxStaffAccounts, isActive);
        this.id = id;
    }

    public SubscriptionPlan(String name, double monthlyPrice, int maxObd2Devices, int maxMonthlySnapshotsPerVehicle, int maxCustomers, int maxStaffAccounts, boolean isActive) {
        if (name == null || name.isBlank()) throw new IllegalArgumentException("core.error.name.required");
        this.id = new SubscriptionPlanId(UUID.randomUUID());
        this.name = name;
        this.monthlyPrice = monthlyPrice;
        this.maxObd2Devices = maxObd2Devices;
        this.maxMonthlySnapshotsPerVehicle = maxMonthlySnapshotsPerVehicle;
        this.maxCustomers = maxCustomers;
        this.maxStaffAccounts = maxStaffAccounts;
        this.isActive = isActive;
    }
}
