package com.forkdevs.driveos.platform.core.domain.model.aggregates;

import com.forkdevs.driveos.platform.core.domain.model.valueobjects.Document;
import com.forkdevs.driveos.platform.core.domain.model.valueobjects.PersonName;
import com.forkdevs.driveos.platform.core.domain.model.valueobjects.Phone;
import com.forkdevs.driveos.platform.core.domain.model.valueobjects.UserId;
import com.forkdevs.driveos.platform.shared.domain.model.valueobjects.CustomerId;
import com.forkdevs.driveos.platform.shared.domain.model.aggregates.AbstractDomainAggregateRoot;

import lombok.Getter;

import java.time.Instant;
import java.util.UUID;

@Getter
public class Customer extends AbstractDomainAggregateRoot<Customer> {

    private CustomerId id;
    private UserId userId;
    private boolean isCorporate;
    private PersonName name;
    private String businessName;
    private Document document;
    private Phone phone;
    private Instant createdAt;
    private Instant updatedAt;
    private Instant deletedAt;
    private Long version;

    public Customer() {}

    public Customer(CustomerId id, UserId userId, boolean isCorporate, PersonName name, String businessName, Document document, Phone phone, Instant createdAt, Instant updatedAt, Instant deletedAt, Long version) {
        this.id = id;
        this.userId = userId;
        this.isCorporate = isCorporate;
        this.name = name;
        this.businessName = businessName;
        this.document = document;
        this.phone = phone;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
        this.deletedAt = deletedAt;
        this.version = version;
    }

    public Customer(UserId userId, boolean isCorporate, PersonName name, String businessName, Document document, Phone phone) {
        if (isCorporate && (businessName == null || businessName.isBlank())) {
            throw new IllegalArgumentException("core.error.businessName.required");
        }
        if (!isCorporate && name == null) {
            throw new IllegalArgumentException("core.error.personName.required");
        }

        this.id = new CustomerId(UUID.randomUUID());
        this.userId = userId;
        this.isCorporate = isCorporate;
        this.name = name;
        this.businessName = businessName;
        this.document = document;
        this.phone = phone;
    }

    public void update(PersonName name, String businessName, Document document, Phone phone) {
        if (this.isCorporate) {
            if (!this.document.getDocumentType().name().equalsIgnoreCase(document.getDocumentType().name())) {
                throw new IllegalArgumentException("core.error.customer.corporateDocumentTypeImmutable");
            }
            if (businessName == null || businessName.isBlank()) {
                throw new IllegalArgumentException("core.error.businessName.required");
            }
            this.businessName = businessName;
        } else {
            if (name == null) {
                throw new IllegalArgumentException("core.error.personName.required");
            }
            this.name = name;
        }
        
        this.document = document;
        this.phone = phone;
    }
}

