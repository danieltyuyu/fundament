package com.forkdevs.driveos.platform.iam.domain.model.commands;

public record GoogleSignInCommand(String idToken) {
    public GoogleSignInCommand {
        if (idToken == null || idToken.isBlank()) {
            throw new IllegalArgumentException("iam.error.idToken.required");
        }
    }
}
