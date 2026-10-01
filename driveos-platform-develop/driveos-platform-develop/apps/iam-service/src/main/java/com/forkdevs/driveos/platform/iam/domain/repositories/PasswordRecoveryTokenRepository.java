package com.forkdevs.driveos.platform.iam.domain.repositories;

import com.forkdevs.driveos.platform.iam.domain.model.entities.PasswordRecoveryToken;

import java.util.Optional;

/**
 * PasswordRecoveryTokenRepository interface.
 * Defines the contract for password recovery token persistence operations.
 */
public interface PasswordRecoveryTokenRepository {
    void save(PasswordRecoveryToken token);
    Optional<PasswordRecoveryToken> findByTokenHash(String tokenHash);
}
