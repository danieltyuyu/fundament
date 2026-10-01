package com.forkdevs.driveos.platform.iam.interfaces.rest.resources;

public record UpdateUserPasswordResource(
        String currentPassword,
        String newPassword
) {
}
