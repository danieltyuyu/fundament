package com.forkdevs.driveos.platform.core.application.internal.queryservices;

import com.forkdevs.driveos.platform.core.domain.model.aggregates.Workshop;
import com.forkdevs.driveos.platform.core.domain.model.queries.GetAllWorkshopsByOwnerIdQuery;
import com.forkdevs.driveos.platform.core.domain.model.queries.GetWorkshopByIdQuery;
import com.forkdevs.driveos.platform.core.domain.model.valueobjects.OwnerId;
import com.forkdevs.driveos.platform.core.domain.model.valueobjects.TaxId;
import com.forkdevs.driveos.platform.core.domain.model.valueobjects.WorkshopId;
import com.forkdevs.driveos.platform.core.domain.repositories.WorkshopRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class WorkshopQueryServiceImplTests {

    @Mock
    private WorkshopRepository workshopRepository;

    @InjectMocks
    private WorkshopQueryServiceImpl workshopQueryService;

    @Test
    void handle_GetWorkshopById_Found_ReturnsWorkshop() {
        WorkshopId workshopId = new WorkshopId(UUID.randomUUID());
        OwnerId ownerId = new OwnerId(UUID.randomUUID());
        Workshop workshop = new Workshop(ownerId, "Taller SAC", "MecaniPro", new TaxId("20123456789"), 5000);

        when(workshopRepository.findById(workshopId)).thenReturn(Optional.of(workshop));

        var result = workshopQueryService.handle(new GetWorkshopByIdQuery(workshopId));

        assertTrue(result.isPresent());
        assertEquals("Taller SAC", result.get().getBusinessName());
        assertEquals(ownerId, result.get().getOwnerId());
        verify(workshopRepository).findById(workshopId);
    }

    @Test
    void handle_GetWorkshopById_NotFound_ReturnsEmpty() {
        WorkshopId workshopId = new WorkshopId(UUID.randomUUID());

        when(workshopRepository.findById(workshopId)).thenReturn(Optional.empty());

        var result = workshopQueryService.handle(new GetWorkshopByIdQuery(workshopId));

        assertTrue(result.isEmpty());
        verify(workshopRepository).findById(workshopId);
    }

    @Test
    void handle_GetAllWorkshopsByOwnerId_ReturnsList() {
        OwnerId ownerId = new OwnerId(UUID.randomUUID());
        Workshop workshop1 = new Workshop(ownerId, "Taller 1 SAC", "MecaniPro 1", new TaxId("20123456789"), 5000);
        Workshop workshop2 = new Workshop(ownerId, "Taller 2 SAC", "MecaniPro 2", new TaxId("20987654321"), 10000);

        when(workshopRepository.findAllByOwnerId(ownerId)).thenReturn(List.of(workshop1, workshop2));

        var result = workshopQueryService.handle(new GetAllWorkshopsByOwnerIdQuery(ownerId));

        assertEquals(2, result.size());
        assertEquals("Taller 1 SAC", result.get(0).getBusinessName());
        assertEquals("Taller 2 SAC", result.get(1).getBusinessName());
        verify(workshopRepository).findAllByOwnerId(ownerId);
    }
}
