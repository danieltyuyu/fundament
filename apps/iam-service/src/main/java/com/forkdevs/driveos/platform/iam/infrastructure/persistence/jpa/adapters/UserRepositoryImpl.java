package com.forkdevs.driveos.platform.iam.infrastructure.persistence.jpa.adapters;

import com.forkdevs.driveos.platform.iam.domain.model.aggregates.User;
import com.forkdevs.driveos.platform.iam.domain.repositories.UserRepository;
import com.forkdevs.driveos.platform.iam.infrastructure.persistence.jpa.assemblers.UserPersistenceAssembler;
import com.forkdevs.driveos.platform.iam.infrastructure.persistence.jpa.entities.UserPersistenceEntity;
import com.forkdevs.driveos.platform.iam.infrastructure.persistence.jpa.repositories.UserPersistenceRepository;

import org.springframework.stereotype.Repository;
import java.util.Optional;
import java.util.UUID;

@Repository
public class UserRepositoryImpl implements UserRepository {

    private final UserPersistenceRepository userPersistenceRepository;

    public UserRepositoryImpl(UserPersistenceRepository userPersistenceRepository) {
        this.userPersistenceRepository = userPersistenceRepository;
    }

    @Override
    public void save(User user) {
        UserPersistenceEntity entity;
        if (user.getId() != null) {
            entity = userPersistenceRepository.findById(user.getId().value()).orElse(new UserPersistenceEntity());
        } else {
            entity = new UserPersistenceEntity();
        }
        
        UserPersistenceAssembler.toEntity(user, entity);
        userPersistenceRepository.save(entity);
    }

    @Override
    public Optional<User> findById(UUID id) {
        return userPersistenceRepository.findById(id).map(UserPersistenceAssembler::toDomain);
    }

    @Override
    public Optional<User> findByEmail(String email) {
        return userPersistenceRepository.findByEmail(email).map(UserPersistenceAssembler::toDomain);
    }

    @Override
    public boolean existsByEmail(String email) {
        return userPersistenceRepository.existsByEmail(email);
    }
}
