package com.forkdevs.driveos.platform.iam.infrastructure.persistence.jpa.repositories;

import com.forkdevs.driveos.platform.iam.infrastructure.persistence.jpa.entities.UserPersistenceEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface UserPersistenceRepository extends JpaRepository<UserPersistenceEntity, UUID> {
    Optional<UserPersistenceEntity> findByEmail(String email);
    boolean existsByEmail(String email);
}
