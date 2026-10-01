package com.forkdevs.driveos.platform.iam.infrastructure.persistence.jpa.entities;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.UUID;

/**
 * JPA persistence entity for password recovery tokens.
 */
@Entity
@Table(name = "password_recovery_tokens")
@Getter
@Setter
@NoArgsConstructor
public class PasswordRecoveryTokenPersistenceEntity {

    @Id
    @Column(columnDefinition = "uuid", updatable = false, nullable = false)
    private UUID id;

    @Column(name = "token_hash", nullable = false)
    private String tokenHash;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "expires_at", nullable = false, updatable = false)
    private LocalDateTime expiresAt;

    @Column(name = "is_used", nullable = false)
    private boolean isUsed = false;

    @Column(name = "user_id", nullable = false)
    private UUID userId;
}
