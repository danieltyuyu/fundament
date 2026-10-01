package com.forkdevs.driveos.platform.iam.domain.repositories;

import com.forkdevs.driveos.platform.iam.domain.model.aggregates.User;

import java.util.Optional;
import java.util.UUID;

/**
 * UserRepository interface.
 * Defines the contract for user persistence operations.
 */
public interface UserRepository {
    void save(User user);
    Optional<User> findById(UUID id);
    Optional<User> findByEmail(String email);
    boolean existsByEmail(String email);
}
