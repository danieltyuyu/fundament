package com.forkdevs.driveos.platform.operations.domain.model.aggregates;

import com.forkdevs.driveos.platform.operations.domain.model.valueobjects.ServiceId;
import com.forkdevs.driveos.platform.shared.domain.model.aggregates.AbstractDomainAggregateRoot;
import com.forkdevs.driveos.platform.shared.domain.model.valueobjects.*;

import lombok.Getter;
import java.time.Instant;
import java.util.UUID;

/**
 * Represents a service offered by the workshop, such as oil change, tire rotation, etc. Each service belongs to a specific branch and has a name and price. Services can be created, updated, and deleted. They are associated with appointments when customers book services for their vehicles.
 * @author Joel Huamani Estefanero
 */
@Getter
public class Service extends AbstractDomainAggregateRoot<Service> {

    private ServiceId id;
    private BranchId branchId;
    private String name;
    private Money price;
    private Instant createdAt;
    private Instant updatedAt;
    private Instant deletedAt;
    private UUID createdBy;
    private UUID updatedBy;
    private Long version;

    public Service() {}

    public Service(ServiceId id, BranchId branchId, String name, Money price, Instant createdAt, Instant updatedAt, Instant deletedAt, UUID createdBy, UUID updatedBy, Long version) {
        this.id = id;
        this.branchId = branchId;
        this.name = name;
        this.price = price;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
        this.deletedAt = deletedAt;
        this.createdBy = createdBy;
        this.updatedBy = updatedBy;
        this.version = version;
    }

    /**
     * Constructor to create a new Service. It generates a new ServiceId and sets the provided branchId, name, and price. The createdAt timestamp is set to the current time, and the version is initialized to 0.
     * @param branchId The ID of the branch to which this service belongs. It is required and cannot be null.
     * @param name The name of the service. It is required and cannot be null or blank.
     * @param price The price of the service. It is required and cannot be null.
     */
    public Service(BranchId branchId, String name, Money price) {
        this.id = new ServiceId(UUID.randomUUID());
        this.branchId = branchId;
        this.name = name;
        this.price = price;
    }

    /**
     * Updates the service's name and price. Both parameters are required and cannot be null or blank. This method allows changing the details of an existing service while keeping its identity intact.
     * @param name The new name of the service. It is required and cannot be null or blank.
     * @param price The new price of the service. It is required and cannot be null.
     */
    public void update(String name, Money price) {
        if (name == null || name.isBlank()) throw new IllegalArgumentException("core.error.name.required");
        this.name = name;
        this.price = price;
    }
}
