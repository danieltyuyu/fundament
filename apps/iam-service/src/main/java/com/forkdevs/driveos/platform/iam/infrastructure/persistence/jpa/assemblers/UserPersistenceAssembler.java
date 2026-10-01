package com.forkdevs.driveos.platform.iam.infrastructure.persistence.jpa.assemblers;

import com.forkdevs.driveos.platform.iam.domain.model.aggregates.User;
import com.forkdevs.driveos.platform.iam.infrastructure.persistence.jpa.entities.UserPersistenceEntity;

import com.forkdevs.driveos.platform.iam.domain.model.valueobjects.EmailAddress;
import com.forkdevs.driveos.platform.iam.domain.model.valueobjects.GoogleId;
import com.forkdevs.driveos.platform.iam.domain.model.valueobjects.Password;
import com.forkdevs.driveos.platform.iam.domain.model.valueobjects.UserId;

public final class UserPersistenceAssembler {

    private UserPersistenceAssembler() {}

    public static UserPersistenceEntity toEntity(User user, UserPersistenceEntity entity) {
        if (entity == null) {
            entity = new UserPersistenceEntity();
        }
        if (user.getId() != null && entity.getVersion() != null) {
            entity.setId(user.getId().value());
        }
        entity.setEmail(user.getEmail().value());
        entity.setPasswordHash(user.getPassword().value());
        entity.setGoogleId(user.getGoogleId() != null ? user.getGoogleId().value() : null);
        entity.setStatus(user.getStatus());
        return entity;
    }

    public static User toDomain(UserPersistenceEntity entity) {
        return new User(
                new UserId(entity.getId()),
                new EmailAddress(entity.getEmail()),
                new Password(entity.getPasswordHash()),
                entity.getGoogleId() != null ? new GoogleId(entity.getGoogleId()) : null,
                entity.getStatus(),
                entity.getCreatedAt(),
                entity.getUpdatedAt(),
                entity.getDeletedAt(),
                entity.getVersion()
        );
    }
}
