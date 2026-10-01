package com.forkdevs.driveos.platform.iam.application.internal.commandservices;

import com.forkdevs.driveos.platform.iam.application.internal.outboundservices.hashing.HashingService;
import com.forkdevs.driveos.platform.iam.domain.model.commands.GeneratePasswordRecoveryTokenCommand;
import com.forkdevs.driveos.platform.iam.domain.model.commands.ResetPasswordCommand;
import com.forkdevs.driveos.platform.iam.domain.model.entities.PasswordRecoveryToken;
import com.forkdevs.driveos.platform.iam.domain.repositories.PasswordRecoveryTokenRepository;
import com.forkdevs.driveos.platform.iam.domain.repositories.UserRepository;
import com.forkdevs.driveos.platform.iam.application.commandservices.PasswordRecoveryCommandService;
import com.forkdevs.driveos.platform.iam.application.internal.outboundservices.email.EmailService;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
public class PasswordRecoveryCommandServiceImpl implements PasswordRecoveryCommandService {

    private final UserRepository userRepository;
    private final PasswordRecoveryTokenRepository tokenRepository;
    private final EmailService emailService;
    private final HashingService hashingService;

    public PasswordRecoveryCommandServiceImpl(UserRepository userRepository, PasswordRecoveryTokenRepository tokenRepository, EmailService emailService, HashingService hashingService) {
        this.userRepository = userRepository;
        this.tokenRepository = tokenRepository;
        this.emailService = emailService;
        this.hashingService = hashingService;
    }

    @Override
    public void handle(GeneratePasswordRecoveryTokenCommand command) {
        var user = userRepository.findByEmail(command.email().value())
                .orElseThrow(() -> new IllegalArgumentException("iam.error.user.notFound"));

        String rawToken = UUID.randomUUID().toString();
        var token = new PasswordRecoveryToken(rawToken, user.getId().value(), 60); // 60 minutes
        tokenRepository.save(token);

        emailService.sendPasswordRecoveryEmail(user.getEmail().value(), rawToken);
    }

    @Override
    public void handle(ResetPasswordCommand command) {
        var tokenEntity = tokenRepository.findByTokenHash(command.token())
                .orElseThrow(() -> new IllegalArgumentException("iam.error.token.invalidOrExpired"));

        if (!tokenEntity.isValid()) {
            throw new IllegalArgumentException("iam.error.token.expiredOrUsed");
        }

        var user = userRepository.findById(tokenEntity.getUserId())
                .orElseThrow(() -> new IllegalArgumentException("iam.error.user.notFound"));

        user.changePassword(new com.forkdevs.driveos.platform.iam.domain.model.valueobjects.Password(hashingService.encode(command.newPassword().value())));
        userRepository.save(user);

        tokenEntity.markAsUsed();
        tokenRepository.save(tokenEntity);
    }
}
