package com.forkdevs.driveos.platform.iam.application.internal.commandservices;

import com.forkdevs.driveos.platform.iam.application.internal.outboundservices.hashing.HashingService;
import com.forkdevs.driveos.platform.iam.application.internal.outboundservices.tokens.TokenService;
import com.forkdevs.driveos.platform.iam.domain.model.aggregates.User;
import com.forkdevs.driveos.platform.iam.domain.model.commands.SignInCommand;
import com.forkdevs.driveos.platform.iam.domain.model.commands.SignUpCommand;
import com.forkdevs.driveos.platform.iam.domain.model.commands.UpdateUserEmailCommand;
import com.forkdevs.driveos.platform.iam.domain.model.commands.UpdateUserPasswordCommand;
import com.forkdevs.driveos.platform.iam.domain.model.queries.AuthenticatedUser;
import com.forkdevs.driveos.platform.iam.domain.repositories.UserRepository;
import com.forkdevs.driveos.platform.iam.application.commandservices.UserCommandService;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.Collections;
import java.util.UUID;

import com.google.api.client.googleapis.auth.oauth2.GoogleIdToken;
import com.google.api.client.googleapis.auth.oauth2.GoogleIdTokenVerifier;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.gson.GsonFactory;
import org.springframework.beans.factory.annotation.Value;

@Service
public class UserCommandServiceImpl implements UserCommandService {
    private final UserRepository userRepository;
    private final HashingService hashingService;
    private final TokenService tokenService;
    private final String googleClientId;

    public UserCommandServiceImpl(UserRepository userRepository, HashingService hashingService, TokenService tokenService, @Value("${google.client.id}") String googleClientId) {
        this.userRepository = userRepository;
        this.hashingService = hashingService;
        this.tokenService = tokenService;
        this.googleClientId = googleClientId;
    }

    @Override
    public Optional<User> handle(SignUpCommand command) {
        if (userRepository.existsByEmail(command.email().value())) {
            throw new IllegalArgumentException("iam.error.email.alreadyInUse");
        }
        var user = new User(command.email(), new com.forkdevs.driveos.platform.iam.domain.model.valueobjects.Password(hashingService.encode(command.password().value())));
        userRepository.save(user);
        return userRepository.findByEmail(command.email().value());
    }

    @Override
    public Optional<AuthenticatedUser> handle(SignInCommand command) {
        var user = userRepository.findByEmail(command.email().value())
                .orElseThrow(() -> new IllegalArgumentException("iam.error.credentials.invalid"));

        if (!hashingService.matches(command.password().value(), user.getPassword().value())) {
            throw new IllegalArgumentException("iam.error.credentials.invalid");
        }

        var claims = new java.util.HashMap<String, Object>();
        if (user.getId() != null && user.getId().value() != null) {
            claims.put("userId", user.getId().value().toString());
        }
        var token = tokenService.generateToken(user.getEmail().value(), claims);
        return Optional.of(new AuthenticatedUser(user, token));
    }

    @Override
    public Optional<AuthenticatedUser> handle(com.forkdevs.driveos.platform.iam.domain.model.commands.GoogleSignInCommand command) {
        try {
            GoogleIdTokenVerifier verifier = new GoogleIdTokenVerifier.Builder(new NetHttpTransport(), new GsonFactory())
                    .setAudience(Collections.singletonList(googleClientId))
                    .build();

            GoogleIdToken idToken = verifier.verify(command.idToken());
            if (idToken == null) {
                throw new IllegalArgumentException("iam.error.googleToken.invalid");
            }

            GoogleIdToken.Payload payload = idToken.getPayload();
            String email = payload.getEmail();
            String googleId = payload.getSubject();

            User user = userRepository.findByEmail(email).orElse(null);

            if (user == null) {
                // Register new user with a secure random password since they use Google
                String randomPassword = String.format("%s%s", UUID.randomUUID(), UUID.randomUUID());
                user = new User(new com.forkdevs.driveos.platform.iam.domain.model.valueobjects.EmailAddress(email), new com.forkdevs.driveos.platform.iam.domain.model.valueobjects.Password(hashingService.encode(randomPassword)), new com.forkdevs.driveos.platform.iam.domain.model.valueobjects.GoogleId(googleId));
                userRepository.save(user);
            } else {
                // If user exists but doesn't have a googleId linked, link it
                if (user.getGoogleId() == null || user.getGoogleId().value().isEmpty()) {
                    user.linkGoogleAccount(new com.forkdevs.driveos.platform.iam.domain.model.valueobjects.GoogleId(googleId));
                    userRepository.save(user);
                }
            }

            var claims = new java.util.HashMap<String, Object>();
            if (user.getId() != null && user.getId().value() != null) {
                claims.put("userId", user.getId().value().toString());
            }
            var token = tokenService.generateToken(user.getEmail().value(), claims);
            return Optional.of(new AuthenticatedUser(user, token));

        } catch (Exception e) {
            throw new IllegalArgumentException("iam.error.googleToken.invalid");
        }
    }

    @Override
    public Optional<AuthenticatedUser> handle(UpdateUserEmailCommand command) {
        var user = userRepository.findById(command.userId().value())
                .orElseThrow(() -> new IllegalArgumentException("iam.error.user.notFound"));

        if (!user.getEmail().value().equals(command.newEmail().value()) && userRepository.existsByEmail(command.newEmail().value())) {
            throw new IllegalArgumentException("iam.error.email.alreadyInUse");
        }

        user.changeEmail(command.newEmail());
        userRepository.save(user);
        
        var token = tokenService.generateToken(user.getEmail().value());
        return Optional.of(new AuthenticatedUser(user, token));
    }

    @Override
    public Optional<User> handle(UpdateUserPasswordCommand command) {
        var user = userRepository.findById(command.userId().value())
                .orElseThrow(() -> new IllegalArgumentException("iam.error.user.notFound"));

        if (!hashingService.matches(command.currentPassword().value(), user.getPassword().value())) {
            throw new IllegalArgumentException("iam.error.currentPassword.invalid");
        }

        user.changePassword(new com.forkdevs.driveos.platform.iam.domain.model.valueobjects.Password(hashingService.encode(command.newPassword().value())));
        userRepository.save(user);
        return Optional.of(user);
    }
}
