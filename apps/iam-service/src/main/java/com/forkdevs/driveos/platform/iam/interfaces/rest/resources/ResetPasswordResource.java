package com.forkdevs.driveos.platform.iam.interfaces.rest.resources;

public record ResetPasswordResource(String token, String newPassword) {
}
