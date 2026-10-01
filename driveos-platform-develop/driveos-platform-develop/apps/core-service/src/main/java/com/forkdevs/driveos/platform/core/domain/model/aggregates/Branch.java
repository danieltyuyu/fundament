package com.forkdevs.driveos.platform.core.domain.model.aggregates;

import com.forkdevs.driveos.platform.core.domain.model.valueobjects.Phone;
import com.forkdevs.driveos.platform.core.domain.model.valueobjects.WorkshopId;
import com.forkdevs.driveos.platform.shared.domain.model.valueobjects.BranchId;
import com.forkdevs.driveos.platform.shared.domain.model.valueobjects.Address;
import com.forkdevs.driveos.platform.shared.domain.model.aggregates.AbstractDomainAggregateRoot;

import lombok.Getter;

import java.time.Instant;
import java.util.UUID;

@Getter
public class Branch extends AbstractDomainAggregateRoot<Branch> {

    private BranchId id;
    private WorkshopId workshopId;
    private String code;
    private String name;
    private Address address;
    private Phone phone;
    private Instant createdAt;
    private Instant updatedAt;
    private Instant deletedAt;
    private UUID createdBy;
    private UUID updatedBy;
    private Long version;

    public Branch() {}

    public Branch(BranchId id, WorkshopId workshopId, String code, String name, Address address, Phone phone, Instant createdAt, Instant updatedAt, Instant deletedAt, UUID createdBy, UUID updatedBy, Long version) {
        this.id = id;
        this.workshopId = workshopId;
        this.code = code;
        this.name = name;
        this.address = address;
        this.phone = phone;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
        this.deletedAt = deletedAt;
        this.createdBy = createdBy;
        this.updatedBy = updatedBy;
        this.version = version;
    }

    public Branch(WorkshopId workshopId, String code, String name, Address address, Phone phone) {
        if (code == null || code.isBlank()) throw new IllegalArgumentException("core.error.code.required");
        if (name == null || name.isBlank()) throw new IllegalArgumentException("core.error.name.required");
        this.id = new BranchId(UUID.randomUUID());
        this.workshopId = workshopId;
        this.code = code;
        this.name = name;
        this.address = address;
        this.phone = phone;
    }

    public void update(String code, String name, Address address, Phone phone) {
        if (code == null || code.isBlank()) throw new IllegalArgumentException("core.error.code.required");
        if (name == null || name.isBlank()) throw new IllegalArgumentException("core.error.name.required");
        
        this.code = code;
        this.name = name;
        this.address = address;
        this.phone = phone;
    }
}

