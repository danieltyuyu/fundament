package com.forkdevs.driveos.platform.iam.interfaces.rest.transform;

import com.forkdevs.driveos.platform.iam.domain.model.queries.AuthenticatedUser;
import com.forkdevs.driveos.platform.iam.interfaces.rest.resources.AuthenticatedUserResource;

public class AuthenticatedUserResourceFromEntityAssembler {
    public static AuthenticatedUserResource toResourceFromEntity(AuthenticatedUser entity) {
        return new AuthenticatedUserResource(
                entity.user().getId().value(),
                entity.user().getEmail().value(),
                entity.token()
        );
    }
}
