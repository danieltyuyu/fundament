package com.forkdevs.driveos.platform.core.application.internal.commandservices;

import com.forkdevs.driveos.platform.core.domain.model.aggregates.Workshop;
import com.forkdevs.driveos.platform.core.domain.model.commands.CreateWorkshopCommand;
import com.forkdevs.driveos.platform.core.domain.model.commands.UpdateWorkshopCommand;
import com.forkdevs.driveos.platform.core.domain.model.valueobjects.OwnerId;
import com.forkdevs.driveos.platform.core.domain.model.valueobjects.TaxId;
import com.forkdevs.driveos.platform.core.domain.model.valueobjects.WorkshopId;
import com.forkdevs.driveos.platform.core.domain.repositories.OwnerRepository;
import com.forkdevs.driveos.platform.core.domain.repositories.WorkshopRepository;
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
class WorkshopCommandServiceImplTests {

    @Mock
    private WorkshopRepository workshopRepository;

    @Mock
    private OwnerRepository ownerRepository;

    @InjectMocks
    private WorkshopCommandServiceImpl workshopCommandService;

    @Test
    void handle_CreateWorkshop_Success() {
        OwnerId ownerId = new OwnerId(UUID.randomUUID());
        TaxId taxId = new TaxId("20123456789");
        CreateWorkshopCommand command = new CreateWorkshopCommand(
                ownerId,
                "Taller Mecanico SAC",
                "MecaniPro",
                taxId,
                5000
        );

        when(ownerRepository.existsById(ownerId)).thenReturn(true);
        when(workshopRepository.save(any(Workshop.class))).thenAnswer(invocation -> invocation.getArgument(0));

        var result = workshopCommandService.handle(command);

        assertTrue(result.isPresent());
        var workshop = result.get();
        assertEquals(ownerId, workshop.getOwnerId());
        assertEquals("Taller Mecanico SAC", workshop.getBusinessName());
        assertEquals("MecaniPro", workshop.getBrandName());
        assertEquals(taxId, workshop.getTaxId());
        assertEquals(5000, workshop.getMileageIntervalConfig());
        verify(ownerRepository).existsById(ownerId);
        verify(workshopRepository).save(any(Workshop.class));
    }

    @Test
    void handle_CreateWorkshop_OwnerNotFound_ThrowsIllegalArgumentException() {
        OwnerId ownerId = new OwnerId(UUID.randomUUID());
        TaxId taxId = new TaxId("20123456789");
        CreateWorkshopCommand command = new CreateWorkshopCommand(
                ownerId,
                "Taller Mecanico SAC",
                "MecaniPro",
                taxId,
                5000
        );

        when(ownerRepository.existsById(ownerId)).thenReturn(false);

        var exception = assertThrows(IllegalArgumentException.class, () -> workshopCommandService.handle(command));
        assertEquals("core.error.owner.notFound", exception.getMessage());
        verify(workshopRepository, never()).save(any());
    }

    @Test
    void handle_UpdateWorkshop_Success() {
        WorkshopId workshopId = new WorkshopId(UUID.randomUUID());
        OwnerId ownerId = new OwnerId(UUID.randomUUID());
        TaxId originalTaxId = new TaxId("20123456789");

        Workshop existingWorkshop = new Workshop(ownerId, "Taller Viejo SAC", "ViejoPro", originalTaxId, 3000);

        TaxId newTaxId = new TaxId("20987654321");
        UpdateWorkshopCommand command = new UpdateWorkshopCommand(
                workshopId,
                "Taller Nuevo SAC",
                "NuevoPro",
                newTaxId,
                10000
        );

        when(workshopRepository.findById(workshopId)).thenReturn(Optional.of(existingWorkshop));
        when(workshopRepository.save(existingWorkshop)).thenReturn(existingWorkshop);

        var result = workshopCommandService.handle(command);

        assertTrue(result.isPresent());
        assertEquals("Taller Nuevo SAC", result.get().getBusinessName());
        assertEquals("NuevoPro", result.get().getBrandName());
        assertEquals(newTaxId, result.get().getTaxId());
        assertEquals(10000, result.get().getMileageIntervalConfig());
        verify(workshopRepository).save(existingWorkshop);
    }

    @Test
    void handle_UpdateWorkshop_NotFound_ThrowsIllegalArgumentException() {
        WorkshopId workshopId = new WorkshopId(UUID.randomUUID());
        TaxId taxId = new TaxId("20123456789");
        UpdateWorkshopCommand command = new UpdateWorkshopCommand(
                workshopId,
                "Taller Nuevo SAC",
                "NuevoPro",
                taxId,
                10000
        );

        when(workshopRepository.findById(workshopId)).thenReturn(Optional.empty());

        var exception = assertThrows(IllegalArgumentException.class, () -> workshopCommandService.handle(command));
        assertEquals("core.error.workshop.notFound", exception.getMessage());
        verify(workshopRepository, never()).save(any());
    }
}
