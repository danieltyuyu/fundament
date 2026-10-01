package com.forkdevs.driveos.platform.core.application.queryservices;

import com.forkdevs.driveos.platform.core.domain.model.queries.GetProfileRolesByUserIdQuery;

import java.util.List;

import com.forkdevs.driveos.platform.core.domain.model.queries.GetProfileByDocumentNumberQuery;
import com.forkdevs.driveos.platform.core.domain.model.queries.responses.ProfileSummary;
import java.util.Optional;

public interface ProfileQueryService {
    List<String> handle(GetProfileRolesByUserIdQuery query);
    Optional<ProfileSummary> handle(GetProfileByDocumentNumberQuery query);
}
