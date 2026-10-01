package com.forkdevs.driveos.platform.core.interfaces.rest;

import com.forkdevs.driveos.platform.core.application.commandservices.OwnerCommandService;
import com.forkdevs.driveos.platform.core.application.queryservices.OwnerQueryService;
import com.forkdevs.driveos.platform.core.domain.model.aggregates.Owner;
import com.forkdevs.driveos.platform.core.domain.model.commands.CreateOwnerCommand;
import com.forkdevs.driveos.platform.core.domain.model.commands.DeleteOwnerCommand;
import com.forkdevs.driveos.platform.core.domain.model.commands.UpdateOwnerCommand;
import com.forkdevs.driveos.platform.core.domain.model.queries.GetOwnerByIdQuery;
import com.forkdevs.driveos.platform.core.domain.model.queries.GetOwnerByUserIdQuery;
import com.forkdevs.driveos.platform.core.domain.model.valueobjects.Document;
import com.forkdevs.driveos.platform.core.domain.model.valueobjects.DocumentType;
import com.forkdevs.driveos.platform.core.domain.model.valueobjects.PersonName;
import com.forkdevs.driveos.platform.core.domain.model.valueobjects.Phone;
import com.forkdevs.driveos.platform.core.domain.model.valueobjects.UserId;
import com.forkdevs.driveos.platform.core.interfaces.rest.resources.CreateOwnerResource;
import com.forkdevs.driveos.platform.core.interfaces.rest.resources.UpdateOwnerResource;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;

import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class OwnersControllerTests {

    @Mock
    private OwnerCommandService ownerCommandService;

    @Mock
    private OwnerQueryService ownerQueryService;

    @InjectMocks
    private OwnersController ownersController;

    @Test
    void createOwner_Success_ReturnsCreated() {
        UUID userId = UUID.randomUUID();
        CreateOwnerResource resource = new CreateOwnerResource(
                userId, "Carlos", "Gomez", "DNI", "87654321", "912345678"
        );

        Owner owner = new Owner(
                new UserId(userId), new PersonName("Carlos", "Gomez"),
                new Document(DocumentType.DNI, "87654321"), new Phone("912345678")
        );

        when(ownerCommandService.handle(any(CreateOwnerCommand.class))).thenReturn(Optional.of(owner));

        var response = ownersController.createOwner(resource);

        assertNotNull(response);
        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals("Carlos", response.getBody().firstName());
    }

    @Test
    void createOwner_Failure_ReturnsBadRequest() {
        CreateOwnerResource resource = new CreateOwnerResource(
                UUID.randomUUID(), "Carlos", "Gomez", "DNI", "87654321", "912345678"
        );

        when(ownerCommandService.handle(any(CreateOwnerCommand.class))).thenReturn(Optional.empty());

        var response = ownersController.createOwner(resource);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
    }

    @Test
    void updateOwner_Success_ReturnsOk() {
        UUID ownerId = UUID.randomUUID();
        UpdateOwnerResource resource = new UpdateOwnerResource(
                "Carlos Alberto", "Gomez", "DNI", "87654321", "999111222"
        );

        Owner owner = new Owner(
                new UserId(UUID.randomUUID()), new PersonName("Carlos Alberto", "Gomez"),
                new Document(DocumentType.DNI, "87654321"), new Phone("999111222")
        );

        when(ownerCommandService.handle(any(UpdateOwnerCommand.class))).thenReturn(Optional.of(owner));

        var response = ownersController.updateOwner(ownerId, resource);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals("Carlos Alberto", response.getBody().firstName());
    }

    @Test
    void updateOwner_Failure_ReturnsBadRequest() {
        UUID ownerId = UUID.randomUUID();
        UpdateOwnerResource resource = new UpdateOwnerResource(
                "Carlos", "Gomez", "DNI", "87654321", "912345678"
        );

        when(ownerCommandService.handle(any(UpdateOwnerCommand.class))).thenReturn(Optional.empty());

        var response = ownersController.updateOwner(ownerId, resource);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
    }

    @Test
    void getOwnerById_Found_ReturnsOk() {
        UUID ownerId = UUID.randomUUID();
        Owner owner = new Owner(
                new UserId(UUID.randomUUID()), new PersonName("Carlos", "Gomez"),
                new Document(DocumentType.DNI, "87654321"), new Phone("912345678")
        );

        when(ownerQueryService.handle(any(GetOwnerByIdQuery.class))).thenReturn(Optional.of(owner));

        var response = ownersController.getOwnerById(ownerId);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
    }

    @Test
    void getOwnerById_NotFound_ReturnsNotFound() {
        UUID ownerId = UUID.randomUUID();

        when(ownerQueryService.handle(any(GetOwnerByIdQuery.class))).thenReturn(Optional.empty());

        var response = ownersController.getOwnerById(ownerId);

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
    }

    @Test
    void getOwnerByUserId_Found_ReturnsOk() {
        UUID userId = UUID.randomUUID();
        Owner owner = new Owner(
                new UserId(userId), new PersonName("Carlos", "Gomez"),
                new Document(DocumentType.DNI, "87654321"), new Phone("912345678")
        );

        when(ownerQueryService.handle(any(GetOwnerByUserIdQuery.class))).thenReturn(Optional.of(owner));

        var response = ownersController.getOwnerByUserId(userId);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
    }

    @Test
    void getOwnerByUserId_NotFound_ReturnsNotFound() {
        UUID userId = UUID.randomUUID();

        when(ownerQueryService.handle(any(GetOwnerByUserIdQuery.class))).thenReturn(Optional.empty());

        var response = ownersController.getOwnerByUserId(userId);

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
    }

    @Test
    void deleteOwner_Success_ReturnsOk() {
        UUID ownerId = UUID.randomUUID();

        var response = ownersController.deleteOwner(ownerId);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        verify(ownerCommandService).handle(any(DeleteOwnerCommand.class));
    }
}
