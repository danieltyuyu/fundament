package com.forkdevs.driveos.platform.core.domain.model.aggregates;

import com.forkdevs.driveos.platform.core.domain.model.valueobjects.Document;
import com.forkdevs.driveos.platform.core.domain.model.valueobjects.OwnerId;
import com.forkdevs.driveos.platform.core.domain.model.valueobjects.PersonName;
import com.forkdevs.driveos.platform.core.domain.model.valueobjects.Phone;
import com.forkdevs.driveos.platform.core.domain.model.valueobjects.UserId;
import com.forkdevs.driveos.platform.shared.domain.model.aggregates.AbstractDomainAggregateRoot;

import lombok.Getter;

import java.time.Instant;
import java.util.UUID;

@Getter
public class Owner extends AbstractDomainAggregateRoot<Owner> {

    private OwnerId id;
    private UserId userId;
    private PersonName name;
    private Document document;
    private Phone phone;
    private Instant createdAt;
    private Instant updatedAt;
    private Instant deletedAt;
    private Long version;

    public Owner() {}

    public Owner(OwnerId id, UserId userId, PersonName name, Document document, Phone phone, Instant createdAt, Instant updatedAt, Instant deletedAt, Long version) {
        this.id = id;
        this.userId = userId;
        this.name = name;
        this.document = document;
        this.phone = phone;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
        this.deletedAt = deletedAt;
        this.version = version;
    }

    public Owner(UserId userId, PersonName name, Document document, Phone phone) {
        this.id = new OwnerId(UUID.randomUUID());
        this.userId = userId;
        this.name = name;
        this.document = document;
        this.phone = phone;
    }

    public void update(PersonName name, Document document, Phone phone) {
        this.name = name;
        this.document = document;
        this.phone = phone;
    }
}
