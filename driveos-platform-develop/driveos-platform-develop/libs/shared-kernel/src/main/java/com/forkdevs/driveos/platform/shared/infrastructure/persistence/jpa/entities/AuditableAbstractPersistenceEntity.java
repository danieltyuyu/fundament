package com.forkdevs.driveos.platform.shared.infrastructure.persistence.jpa.entities;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.Instant;
import java.util.UUID;

/**
 * Base JPA persistence entity for all persistence entities that require auditing.
 *
 * <p>Provides {@code id}, {@code createdAt}, and {@code updatedAt} fields.
 * This class intentionally lives in the infrastructure layer to keep JPA and
 * Spring Data auditing concerns out of the domain model.</p>
 *
 * <p>All bounded-context JPA persistence entities should extend this class
 * instead of placing {@code @Id} and auditing fields directly.</p>
 */
@Getter
@MappedSuperclass
@EntityListeners(AuditingEntityListener.class)
public abstract class AuditableAbstractPersistenceEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Setter
    @Column(columnDefinition = "uuid", updatable = false, nullable = false)
    private UUID id;

    @CreatedDate
    @Setter
    @Column(name = "created_at", nullable = false, updatable = false)
    private Instant createdAt;

    @LastModifiedDate
    @Setter
    @Column(name = "updated_at", nullable = false)
    private Instant updatedAt;
}
