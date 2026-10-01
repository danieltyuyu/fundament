package com.forkdevs.driveos.platform.iam.domain.model.aggregates;

import com.forkdevs.driveos.platform.shared.domain.model.aggregates.AbstractDomainAggregateRoot;
import com.forkdevs.driveos.platform.iam.domain.model.valueobjects.EmailAddress;
import com.forkdevs.driveos.platform.iam.domain.model.valueobjects.GoogleId;
import com.forkdevs.driveos.platform.iam.domain.model.valueobjects.Password;
import com.forkdevs.driveos.platform.iam.domain.model.valueobjects.UserId;

import lombok.Getter;
import java.time.Instant;
import java.util.UUID;

/**
 * User aggregate root.
 * Represents an authentication account in the system.
 */
@Getter
public class User extends AbstractDomainAggregateRoot<User> {

    private UserId id;
    private EmailAddress email;
    private Password password;
    private GoogleId googleId;
    private String status;
    private Instant createdAt;
    private Instant updatedAt;
    private Instant deletedAt;
    private Long version;

    public User() {
        this.status = "ACTIVE";
    }

    public User(UserId id, EmailAddress email, Password password, GoogleId googleId, String status, Instant createdAt, Instant updatedAt, Instant deletedAt, Long version) {
        this.id = id;
        this.email = email;
        this.password = password;
        this.googleId = googleId;
        this.status = status;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
        this.deletedAt = deletedAt;
        this.version = version;
    }

    public User (EmailAddress email, Password password, GoogleId googleId, String status) {
        this.id = new UserId(UUID.randomUUID());
        this.email = email;
        this.password = password;
        this.googleId = googleId;
        this.status = status;
    }

    public User(EmailAddress email, Password password) {
        this();
        this.email = email;
        this.password = password;
    }

    public User(EmailAddress email, Password password, GoogleId googleId) {
        this();
        this.email = email;
        this.password = password;
        this.googleId = googleId;
    }
    
    public void deactivate() {
        this.status = "INACTIVE";
    }

    public void changePassword(Password newPassword) {
        this.password = newPassword;
    }

    public void changeEmail(EmailAddress newEmail) {
        this.email = newEmail;
    }

    public void linkGoogleAccount(GoogleId googleId) {
        this.googleId = googleId;
    }

    public void setUserId(UserId id) {
        this.id = id;
    }
}
