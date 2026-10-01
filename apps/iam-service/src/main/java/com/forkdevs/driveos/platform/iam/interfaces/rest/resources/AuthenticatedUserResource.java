package com.forkdevs.driveos.platform.iam.interfaces.rest.resources;

import java.util.UUID;

public record AuthenticatedUserResource(UUID id, String email, String token) {
}
