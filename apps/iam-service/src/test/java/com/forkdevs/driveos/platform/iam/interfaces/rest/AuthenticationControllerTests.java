package com.forkdevs.driveos.platform.iam.interfaces.rest;

import com.forkdevs.driveos.platform.iam.application.commandservices.PasswordRecoveryCommandService;
import com.forkdevs.driveos.platform.iam.application.commandservices.UserCommandService;
import com.forkdevs.driveos.platform.iam.domain.model.aggregates.User;
import com.forkdevs.driveos.platform.iam.domain.model.queries.AuthenticatedUser;
import com.forkdevs.driveos.platform.iam.domain.model.valueobjects.EmailAddress;
import com.forkdevs.driveos.platform.iam.domain.model.valueobjects.Password;
import com.forkdevs.driveos.platform.iam.interfaces.rest.resources.SignInResource;
import org.junit.jupiter.api.Test;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class AuthenticationControllerTests {

    @Test
    void signInSuccessReturnsOkTokenResponse() {
        var userCommandService = mock(UserCommandService.class);
        var passwordRecoveryCommandService = mock(PasswordRecoveryCommandService.class);
        var controller = new AuthenticationController(userCommandService, passwordRecoveryCommandService);

        var dummyUser = new User(new EmailAddress("test@driveos.com"), new Password("encoded-pwd"));
        dummyUser.setUserId(new com.forkdevs.driveos.platform.iam.domain.model.valueobjects.UserId(java.util.UUID.randomUUID()));
        var authUser = new AuthenticatedUser(dummyUser, "jwt-dummy-token");

        when(userCommandService.handle(any(com.forkdevs.driveos.platform.iam.domain.model.commands.SignInCommand.class))).thenReturn(Optional.of(authUser));

        var response = controller.signIn(new SignInResource("test@driveos.com", "secret123"));

        assertNotNull(response);
        assertEquals(200, response.getStatusCode().value());
        assertNotNull(response.getBody());
        assertEquals("test@driveos.com", response.getBody().email());
        assertEquals("jwt-dummy-token", response.getBody().token());
    }
}
