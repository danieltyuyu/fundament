package com.forkdevs.driveos.platform.iam.interfaces.rest.transform;

import com.forkdevs.driveos.platform.iam.domain.model.commands.GoogleSignInCommand;
import com.forkdevs.driveos.platform.iam.interfaces.rest.resources.GoogleSignInResource;

public class GoogleSignInCommandFromResourceAssembler {
    public static GoogleSignInCommand toCommandFromResource(GoogleSignInResource resource) {
        return new GoogleSignInCommand(resource.idToken());
    }
}
