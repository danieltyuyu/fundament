package com.forkdevs.driveos.platform.core.application.internal.queryservices;

import com.forkdevs.driveos.platform.core.domain.model.aggregates.Owner;
import com.forkdevs.driveos.platform.core.domain.model.queries.GetOwnerByIdQuery;
import com.forkdevs.driveos.platform.core.domain.model.queries.GetOwnerByUserIdQuery;
import com.forkdevs.driveos.platform.core.domain.model.valueobjects.Document;
import com.forkdevs.driveos.platform.core.domain.model.valueobjects.DocumentType;
import com.forkdevs.driveos.platform.core.domain.model.valueobjects.OwnerId;
import com.forkdevs.driveos.platform.core.domain.model.valueobjects.PersonName;
import com.forkdevs.driveos.platform.core.domain.model.valueobjects.Phone;
import com.forkdevs.driveos.platform.core.domain.model.valueobjects.UserId;
import com.forkdevs.driveos.platform.core.domain.repositories.OwnerRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class OwnerQueryServiceImplTests {

    @Mock
    private OwnerRepository ownerRepository;

    @InjectMocks
    private OwnerQueryServiceImpl ownerQueryService;

    @Test
    void handle_GetOwnerById_Found_ReturnsOwner() {
        OwnerId ownerId = new OwnerId(UUID.randomUUID());
        UserId userId = new UserId(UUID.randomUUID());
        Owner owner = new Owner(
                userId,
                new PersonName("Carlos", "Gomez"),
                new Document(DocumentType.DNI, "87654321"),
                new Phone("912345678")
        );

        when(ownerRepository.findById(ownerId)).thenReturn(Optional.of(owner));

        var result = ownerQueryService.handle(new GetOwnerByIdQuery(ownerId));

        assertTrue(result.isPresent());
        assertEquals(userId, result.get().getUserId());
        assertEquals("Carlos Gomez", result.get().getName().getFullName());
        verify(ownerRepository).findById(ownerId);
    }

    @Test
    void handle_GetOwnerById_NotFound_ReturnsEmpty() {
        OwnerId ownerId = new OwnerId(UUID.randomUUID());

        when(ownerRepository.findById(ownerId)).thenReturn(Optional.empty());

        var result = ownerQueryService.handle(new GetOwnerByIdQuery(ownerId));

        assertTrue(result.isEmpty());
        verify(ownerRepository).findById(ownerId);
    }

    @Test
    void handle_GetOwnerByUserId_Found_ReturnsOwner() {
        UserId userId = new UserId(UUID.randomUUID());
        Owner owner = new Owner(
                userId,
                new PersonName("Carlos", "Gomez"),
                new Document(DocumentType.DNI, "87654321"),
                new Phone("912345678")
        );

        when(ownerRepository.findByUserId(userId)).thenReturn(Optional.of(owner));

        var result = ownerQueryService.handle(new GetOwnerByUserIdQuery(userId));

        assertTrue(result.isPresent());
        assertEquals(userId, result.get().getUserId());
        verify(ownerRepository).findByUserId(userId);
    }

    @Test
    void handle_GetOwnerByUserId_NotFound_ReturnsEmpty() {
        UserId userId = new UserId(UUID.randomUUID());

        when(ownerRepository.findByUserId(userId)).thenReturn(Optional.empty());

        var result = ownerQueryService.handle(new GetOwnerByUserIdQuery(userId));

        assertTrue(result.isEmpty());
        verify(ownerRepository).findByUserId(userId);
    }
}
