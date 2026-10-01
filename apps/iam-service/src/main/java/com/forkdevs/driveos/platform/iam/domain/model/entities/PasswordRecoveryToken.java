package com.forkdevs.driveos.platform.iam.domain.model.entities;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.UUID;

/**
 * PasswordRecoveryToken entity.
 * Represents a token generated for recovering a user's password.
 */
@Getter
public class PasswordRecoveryToken {

    @Setter
    private UUID id;

    @Setter
    private String tokenHash;

    @Setter
    private LocalDateTime createdAt;

    @Setter
    private LocalDateTime expiresAt;

    @Setter
    private boolean isUsed;

    @Setter
    private UUID userId;

    public PasswordRecoveryToken() {
    }

    public PasswordRecoveryToken(String tokenHash, UUID userId, long expirationMinutes) {
        this.id = UUID.randomUUID();
        this.tokenHash = tokenHash;
        this.userId = userId;
        this.createdAt = LocalDateTime.now();
        this.expiresAt = this.createdAt.plusMinutes(expirationMinutes);
        this.isUsed = false;
    }

    public boolean isValid() {
        return !isUsed && LocalDateTime.now().isBefore(expiresAt);
    }

    public void markAsUsed() {
        this.isUsed = true;
    }
}
