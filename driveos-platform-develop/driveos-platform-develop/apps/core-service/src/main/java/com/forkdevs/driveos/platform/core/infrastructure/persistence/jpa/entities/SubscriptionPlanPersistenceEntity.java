package com.forkdevs.driveos.platform.core.infrastructure.persistence.jpa.entities;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.UUID;

@Entity
@Table(name = "subscription_plans")
@Getter
@Setter
@NoArgsConstructor
public class SubscriptionPlanPersistenceEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(columnDefinition = "uuid", updatable = false, nullable = false)
    private UUID id;

    @Column(nullable = false, unique = true)
    private String name;

    @Column(name = "monthly_price", nullable = false)
    private double monthlyPrice;

    @Column(name = "max_obd2_devices", nullable = false)
    private int maxObd2Devices;

    @Column(name = "max_monthly_snapshots_per_vehicle", nullable = false)
    private int maxMonthlySnapshotsPerVehicle;

    @Column(name = "max_customers", nullable = false)
    private int maxCustomers;

    @Column(name = "max_staff_accounts", nullable = false)
    private int maxStaffAccounts;

    @Column(name = "is_active", nullable = false)
    private boolean isActive;
}
