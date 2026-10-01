package com.forkdevs.driveos.platform.core.application.internal.commandservices;

import com.forkdevs.driveos.platform.core.domain.model.aggregates.Owner;
import com.forkdevs.driveos.platform.core.domain.model.commands.CreateOwnerCommand;
import com.forkdevs.driveos.platform.core.domain.model.commands.DeleteOwnerCommand;
import com.forkdevs.driveos.platform.core.domain.model.commands.UpdateOwnerCommand;
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
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class OwnerCommandServiceImplTests {

    @Mock
    private OwnerRepository ownerRepository;

    @InjectMocks
    private OwnerCommandServiceImpl ownerCommandService;

    @Test
    void handle_CreateOwner_Success() {
        UserId userId = new UserId(UUID.randomUUID());
        PersonName name = new PersonName("Carlos", "Gomez");
        Document document = new Document(DocumentType.DNI, "87654321");
        Phone phone = new Phone("912345678");

        CreateOwnerCommand command = new CreateOwnerCommand(userId, name, document, phone);

        when(ownerRepository.existsByUserId(userId)).thenReturn(false);
        when(ownerRepository.save(any(Owner.class))).thenAnswer(invocation -> invocation.getArgument(0));

        var result = ownerCommandService.handle(command);

        assertTrue(result.isPresent());
        var owner = result.get();
        assertEquals(userId, owner.getUserId());
        assertEquals("Carlos Gomez", owner.getName().getFullName());
        assertEquals("87654321", owner.getDocument().getDocumentNumber());
        assertEquals("912345678", owner.getPhone().value());
        verify(ownerRepository).existsByUserId(userId);
        verify(ownerRepository).save(any(Owner.class));
    }

    @Test
    void handle_CreateOwner_ProfileAlreadyExists_ThrowsIllegalArgumentException() {
        UserId userId = new UserId(UUID.randomUUID());
        PersonName name = new PersonName("Carlos", "Gomez");
        Document document = new Document(DocumentType.DNI, "87654321");
        Phone phone = new Phone("912345678");

        CreateOwnerCommand command = new CreateOwnerCommand(userId, name, document, phone);

        when(ownerRepository.existsByUserId(userId)).thenReturn(true);

        var exception = assertThrows(IllegalArgumentException.class, () -> ownerCommandService.handle(command));
        assertEquals("core.error.owner.profileAlreadyExists", exception.getMessage());
        verify(ownerRepository, never()).save(any());
    }

    @Test
    void handle_UpdateOwner_Success() {
        OwnerId ownerId = new OwnerId(UUID.randomUUID());
        UserId userId = new UserId(UUID.randomUUID());
        PersonName originalName = new PersonName("Carlos", "Gomez");
        Document document = new Document(DocumentType.DNI, "87654321");
        Phone phone = new Phone("912345678");

        Owner existingOwner = new Owner(userId, originalName, document, phone);

        PersonName updatedName = new PersonName("Carlos Alberto", "Gomez");
        Phone updatedPhone = new Phone("999111222");
        UpdateOwnerCommand command = new UpdateOwnerCommand(ownerId, updatedName, document, updatedPhone);

        when(ownerRepository.findById(ownerId)).thenReturn(Optional.of(existingOwner));
        when(ownerRepository.save(existingOwner)).thenReturn(existingOwner);

        var result = ownerCommandService.handle(command);

        assertTrue(result.isPresent());
        assertEquals("Carlos Alberto Gomez", result.get().getName().getFullName());
        assertEquals("999111222", result.get().getPhone().value());
        verify(ownerRepository).save(existingOwner);
    }

    @Test
    void handle_UpdateOwner_NotFound_ThrowsIllegalArgumentException() {
        OwnerId ownerId = new OwnerId(UUID.randomUUID());
        UpdateOwnerCommand command = new UpdateOwnerCommand(
                ownerId,
                new PersonName("Carlos", "Gomez"),
                new Document(DocumentType.DNI, "87654321"),
                new Phone("912345678")
        );

        when(ownerRepository.findById(ownerId)).thenReturn(Optional.empty());

        var exception = assertThrows(IllegalArgumentException.class, () -> ownerCommandService.handle(command));
        assertEquals("core.error.owner.notFound", exception.getMessage());
        verify(ownerRepository, never()).save(any());
    }

    @Test
    void handle_DeleteOwner_Success() {
        OwnerId ownerId = new OwnerId(UUID.randomUUID());
        Owner existingOwner = mock(Owner.class);

        when(ownerRepository.findById(ownerId)).thenReturn(Optional.of(existingOwner));

        ownerCommandService.handle(new DeleteOwnerCommand(ownerId));

        verify(ownerRepository).delete(existingOwner);
    }

    @Test
    void handle_DeleteOwner_NotFound_ThrowsIllegalArgumentException() {
        OwnerId ownerId = new OwnerId(UUID.randomUUID());

        when(ownerRepository.findById(ownerId)).thenReturn(Optional.empty());

        var exception = assertThrows(IllegalArgumentException.class, () ->
                ownerCommandService.handle(new DeleteOwnerCommand(ownerId)));
        assertEquals("core.error.owner.notFound", exception.getMessage());
        verify(ownerRepository, never()).delete(any());
    }
}
