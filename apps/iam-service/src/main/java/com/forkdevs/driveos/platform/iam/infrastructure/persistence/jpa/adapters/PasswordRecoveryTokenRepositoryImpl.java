package com.forkdevs.driveos.platform.iam.infrastructure.persistence.jpa.adapters;

import com.forkdevs.driveos.platform.iam.domain.model.entities.PasswordRecoveryToken;
import com.forkdevs.driveos.platform.iam.domain.repositories.PasswordRecoveryTokenRepository;
import com.forkdevs.driveos.platform.iam.infrastructure.persistence.jpa.assemblers.PasswordRecoveryTokenPersistenceAssembler;
import com.forkdevs.driveos.platform.iam.infrastructure.persistence.jpa.entities.PasswordRecoveryTokenPersistenceEntity;
import com.forkdevs.driveos.platform.iam.infrastructure.persistence.jpa.repositories.PasswordRecoveryTokenPersistenceRepository;

import org.springframework.stereotype.Repository;
import java.util.Optional;

@Repository
public class PasswordRecoveryTokenRepositoryImpl implements PasswordRecoveryTokenRepository {

    private final PasswordRecoveryTokenPersistenceRepository passwordRecoveryTokenPersistenceRepository;

    public PasswordRecoveryTokenRepositoryImpl(PasswordRecoveryTokenPersistenceRepository passwordRecoveryTokenPersistenceRepository) {
        this.passwordRecoveryTokenPersistenceRepository = passwordRecoveryTokenPersistenceRepository;
    }

    @Override
    public void save(PasswordRecoveryToken token) {
        PasswordRecoveryTokenPersistenceEntity entity;
        if (token.getId() != null) {
            entity = passwordRecoveryTokenPersistenceRepository.findById(token.getId()).orElse(new PasswordRecoveryTokenPersistenceEntity());
        } else {
            entity = new PasswordRecoveryTokenPersistenceEntity();
        }
        
        PasswordRecoveryTokenPersistenceAssembler.toEntity(token, entity);
        passwordRecoveryTokenPersistenceRepository.save(entity);
    }

    @Override
    public Optional<PasswordRecoveryToken> findByTokenHash(String tokenHash) {
        return passwordRecoveryTokenPersistenceRepository.findByTokenHash(tokenHash).map(PasswordRecoveryTokenPersistenceAssembler::toDomain);
    }
}
