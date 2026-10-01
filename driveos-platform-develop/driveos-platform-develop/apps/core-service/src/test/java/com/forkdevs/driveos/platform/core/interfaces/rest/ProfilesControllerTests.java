package com.forkdevs.driveos.platform.core.interfaces.rest;

import com.forkdevs.driveos.platform.core.application.queryservices.ProfileQueryService;
import com.forkdevs.driveos.platform.core.domain.model.queries.GetProfileByDocumentNumberQuery;
import com.forkdevs.driveos.platform.core.domain.model.queries.GetProfileRolesByUserIdQuery;
import com.forkdevs.driveos.platform.core.domain.model.queries.responses.ProfileSummary;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ProfilesControllerTests {

    @Mock
    private ProfileQueryService profileQueryService;

    @InjectMocks
    private ProfilesController profilesController;

    @Test
    void getUserProfileRoles_ReturnsOkList() {
        UUID userId = UUID.randomUUID();

        when(profileQueryService.handle(any(GetProfileRolesByUserIdQuery.class)))
                .thenReturn(List.of("CUSTOMER", "OWNER"));

        var response = profilesController.getUserProfileRoles(userId);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(2, response.getBody().size());
        assertTrue(response.getBody().contains("CUSTOMER"));
        assertTrue(response.getBody().contains("OWNER"));
    }

    @Test
    void getProfileByDocumentNumber_Found_ReturnsOk() {
        String doc = "12345678";
        ProfileSummary summary = new ProfileSummary(
                UUID.randomUUID(), UUID.randomUUID(), "Juan", "Perez", "DNI", doc, "CUSTOMER"
        );

        when(profileQueryService.handle(any(GetProfileByDocumentNumberQuery.class)))
                .thenReturn(Optional.of(summary));

        var response = profilesController.getProfileByDocumentNumber(doc);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals("Juan", response.getBody().firstName());
        assertEquals("CUSTOMER", response.getBody().profileType());
    }

    @Test
    void getProfileByDocumentNumber_NotFound_ReturnsNotFound() {
        String doc = "99999999";

        when(profileQueryService.handle(any(GetProfileByDocumentNumberQuery.class)))
                .thenReturn(Optional.empty());

        var response = profilesController.getProfileByDocumentNumber(doc);

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
    }
}
