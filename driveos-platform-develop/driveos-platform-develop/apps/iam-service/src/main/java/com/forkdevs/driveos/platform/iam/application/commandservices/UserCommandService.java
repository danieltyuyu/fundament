package com.forkdevs.driveos.platform.iam.application.commandservices;

import com.forkdevs.driveos.platform.iam.domain.model.aggregates.User;
import com.forkdevs.driveos.platform.iam.domain.model.commands.SignInCommand;
import com.forkdevs.driveos.platform.iam.domain.model.commands.SignUpCommand;
import com.forkdevs.driveos.platform.iam.domain.model.commands.UpdateUserEmailCommand;
import com.forkdevs.driveos.platform.iam.domain.model.commands.UpdateUserPasswordCommand;
import com.forkdevs.driveos.platform.iam.domain.model.commands.GoogleSignInCommand;
import com.forkdevs.driveos.platform.iam.domain.model.queries.AuthenticatedUser;

import java.util.Optional;

public interface UserCommandService {
    Optional<User> handle(SignUpCommand command);
    Optional<AuthenticatedUser> handle(SignInCommand command);
    Optional<AuthenticatedUser> handle(GoogleSignInCommand command);
    Optional<AuthenticatedUser> handle(UpdateUserEmailCommand command);
    Optional<User> handle(UpdateUserPasswordCommand command);
}
