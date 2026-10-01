package com.forkdevs.driveos.platform.iam.interfaces.rest;

import com.forkdevs.driveos.platform.iam.application.commandservices.UserCommandService;
import com.forkdevs.driveos.platform.iam.application.queryservices.UserQueryService;
import com.forkdevs.driveos.platform.iam.domain.model.queries.GetUserByIdQuery;
import com.forkdevs.driveos.platform.iam.interfaces.rest.resources.UpdateUserEmailResource;
import com.forkdevs.driveos.platform.iam.interfaces.rest.resources.UpdateUserPasswordResource;
import com.forkdevs.driveos.platform.iam.interfaces.rest.resources.UserResource;
import com.forkdevs.driveos.platform.iam.interfaces.rest.transform.UpdateUserEmailCommandFromResourceAssembler;
import com.forkdevs.driveos.platform.iam.interfaces.rest.transform.UpdateUserPasswordCommandFromResourceAssembler;
import com.forkdevs.driveos.platform.iam.interfaces.rest.transform.UserResourceFromEntityAssembler;
import com.forkdevs.driveos.platform.iam.interfaces.rest.resources.AuthenticatedUserResource;
import com.forkdevs.driveos.platform.iam.interfaces.rest.transform.AuthenticatedUserResourceFromEntityAssembler;
import com.forkdevs.driveos.platform.iam.interfaces.rest.resources.SignUpResource;
import com.forkdevs.driveos.platform.iam.interfaces.rest.transform.SignUpCommandFromResourceAssembler;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/v1/users")
@Tag(name = "Users", description = "User Management Endpoints")
public class UsersController {

    private final UserCommandService userCommandService;
    private final UserQueryService userQueryService;

    public UsersController(UserCommandService userCommandService, UserQueryService userQueryService) {
        this.userCommandService = userCommandService;
        this.userQueryService = userQueryService;
    }

    @PostMapping
    @Operation(summary = "Sign up", description = "Register a new user")
    public ResponseEntity<UserResource> signUp(@RequestBody SignUpResource signUpResource) {
        var signUpCommand = SignUpCommandFromResourceAssembler.toCommandFromResource(signUpResource);
        var user = userCommandService.handle(signUpCommand);
        if (user.isEmpty()) {
            return ResponseEntity.badRequest().build();
        }
        var userResource = UserResourceFromEntityAssembler.toResourceFromEntity(user.get());
        return new ResponseEntity<>(userResource, HttpStatus.CREATED);
    }

    @Operation(summary = "Get current authenticated user profile", description = "Retrieves profile details of the authenticated user")
    @GetMapping("/me")
    public ResponseEntity<UserResource> getCurrentUser(org.springframework.security.core.Authentication authentication) {
        if (authentication == null || authentication.getName() == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
        var query = new com.forkdevs.driveos.platform.iam.domain.model.queries.GetUserByEmailQuery(
                new com.forkdevs.driveos.platform.iam.domain.model.valueobjects.EmailAddress(authentication.getName())
        );
        var user = userQueryService.handle(query);
        if (user.isEmpty()) {
            return ResponseEntity.notFound().build();
        }
        var userResource = UserResourceFromEntityAssembler.toResourceFromEntity(user.get());
        return ResponseEntity.ok(userResource);
    }

    @Operation(summary = "Get user by ID", description = "Retrieves the details of a specific user")
    @GetMapping("/{userId}")
    public ResponseEntity<UserResource> getUserById(@PathVariable UUID userId) {
        var query = new GetUserByIdQuery(new com.forkdevs.driveos.platform.iam.domain.model.valueobjects.UserId(userId));
        var user = userQueryService.handle(query);

        if (user.isEmpty()) {
            return ResponseEntity.notFound().build();
        }

        var userResource = UserResourceFromEntityAssembler.toResourceFromEntity(user.get());
        return ResponseEntity.ok(userResource);
    }

    @Operation(summary = "Get user by email", description = "Retrieves the details of a specific user using their email address")
    @GetMapping
    public ResponseEntity<UserResource> getUserByEmail(@RequestParam(name = "email") String email) {
        var query = new com.forkdevs.driveos.platform.iam.domain.model.queries.GetUserByEmailQuery(new com.forkdevs.driveos.platform.iam.domain.model.valueobjects.EmailAddress(email));
        var user = userQueryService.handle(query);

        if (user.isEmpty()) {
            return ResponseEntity.notFound().build();
        }

        var userResource = UserResourceFromEntityAssembler.toResourceFromEntity(user.get());
        return ResponseEntity.ok(userResource);
    }

    @Operation(summary = "Update user email", description = "Updates the email address of a specific user and returns a new authentication token")
    @PutMapping("/{userId}/email")
    public ResponseEntity<AuthenticatedUserResource> updateUserEmail(@PathVariable UUID userId, @RequestBody UpdateUserEmailResource resource) {
        var command = UpdateUserEmailCommandFromResourceAssembler.toCommandFromResource(userId, resource);
        var authenticatedUser = userCommandService.handle(command);

        if (authenticatedUser.isEmpty()) {
            return ResponseEntity.badRequest().build();
        }

        var authenticatedUserResource = AuthenticatedUserResourceFromEntityAssembler.toResourceFromEntity(authenticatedUser.get());
        return ResponseEntity.ok(authenticatedUserResource);
    }

    @Operation(summary = "Update user password", description = "Updates the password of a specific user")
    @PutMapping("/{userId}/password")
    public ResponseEntity<?> updateUserPassword(@PathVariable UUID userId, @RequestBody UpdateUserPasswordResource resource) {
        var command = UpdateUserPasswordCommandFromResourceAssembler.toCommandFromResource(userId, resource);
        var user = userCommandService.handle(command);

        if (user.isEmpty()) {
            return ResponseEntity.badRequest().build();
        }

        return ResponseEntity.ok().build();
    }
}
