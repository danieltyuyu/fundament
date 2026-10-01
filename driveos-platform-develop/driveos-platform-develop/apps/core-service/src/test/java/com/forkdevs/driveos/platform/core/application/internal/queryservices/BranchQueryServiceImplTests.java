package com.forkdevs.driveos.platform.core.application.internal.queryservices;

import com.forkdevs.driveos.platform.core.domain.model.aggregates.Branch;
import com.forkdevs.driveos.platform.core.domain.model.aggregates.Workshop;
import com.forkdevs.driveos.platform.core.domain.model.queries.GetAllBranchesByWorkshopIdQuery;
import com.forkdevs.driveos.platform.core.domain.model.queries.GetBranchByIdQuery;
import com.forkdevs.driveos.platform.core.domain.model.queries.GetIssuerTaxIdByBranchIdQuery;
import com.forkdevs.driveos.platform.core.domain.model.valueobjects.OwnerId;
import com.forkdevs.driveos.platform.core.domain.model.valueobjects.Phone;
import com.forkdevs.driveos.platform.core.domain.model.valueobjects.TaxId;
import com.forkdevs.driveos.platform.core.domain.model.valueobjects.WorkshopId;
import com.forkdevs.driveos.platform.core.domain.repositories.BranchRepository;
import com.forkdevs.driveos.platform.core.domain.repositories.WorkshopRepository;
import com.forkdevs.driveos.platform.shared.domain.model.valueobjects.Address;
import com.forkdevs.driveos.platform.shared.domain.model.valueobjects.BranchId;
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
class BranchQueryServiceImplTests {

    @Mock
    private BranchRepository branchRepository;

    @Mock
    private WorkshopRepository workshopRepository;

    @InjectMocks
    private BranchQueryServiceImpl branchQueryService;

    @Test
    void handle_GetBranchById_Found_ReturnsBranch() {
        BranchId branchId = new BranchId(UUID.randomUUID());
        WorkshopId workshopId = new WorkshopId(UUID.randomUUID());
        Branch branch = new Branch(
                workshopId,
                "BR-001",
                "Sede Central",
                new Address("Av. Javier Prado 1234"),
                new Phone("987654321")
        );

        when(branchRepository.findById(branchId)).thenReturn(Optional.of(branch));

        var result = branchQueryService.handle(new GetBranchByIdQuery(branchId));

        assertTrue(result.isPresent());
        assertEquals("BR-001", result.get().getCode());
        assertEquals("Sede Central", result.get().getName());
        verify(branchRepository).findById(branchId);
    }

    @Test
    void handle_GetBranchById_NotFound_ReturnsEmpty() {
        BranchId branchId = new BranchId(UUID.randomUUID());

        when(branchRepository.findById(branchId)).thenReturn(Optional.empty());

        var result = branchQueryService.handle(new GetBranchByIdQuery(branchId));

        assertTrue(result.isEmpty());
        verify(branchRepository).findById(branchId);
    }

    @Test
    void handle_GetAllBranchesByWorkshopId_ReturnsList() {
        WorkshopId workshopId = new WorkshopId(UUID.randomUUID());
        Branch branch1 = new Branch(workshopId, "BR-001", "Sede 1", new Address("Av. Direccion 1"), new Phone("987654321"));
        Branch branch2 = new Branch(workshopId, "BR-002", "Sede 2", new Address("Av. Direccion 2"), new Phone("987654322"));

        when(branchRepository.findAllByWorkshopId(workshopId)).thenReturn(List.of(branch1, branch2));

        var result = branchQueryService.handle(new GetAllBranchesByWorkshopIdQuery(workshopId));

        assertEquals(2, result.size());
        assertEquals("BR-001", result.get(0).getCode());
        assertEquals("BR-002", result.get(1).getCode());
        verify(branchRepository).findAllByWorkshopId(workshopId);
    }

    @Test
    void handle_GetIssuerTaxIdByBranchId_Success_ReturnsTaxId() {
        BranchId branchId = new BranchId(UUID.randomUUID());
        WorkshopId workshopId = new WorkshopId(UUID.randomUUID());
        Branch branch = new Branch(workshopId, "BR-001", "Sede Central", new Address("Av. Central 123"), new Phone("987654321"));
        Workshop workshop = new Workshop(new OwnerId(UUID.randomUUID()), "Taller SAC", "MecaniPro", new TaxId("20123456789"), 5000);

        when(branchRepository.findById(branchId)).thenReturn(Optional.of(branch));
        when(workshopRepository.findById(workshopId)).thenReturn(Optional.of(workshop));

        var result = branchQueryService.handle(new GetIssuerTaxIdByBranchIdQuery(branchId));

        assertTrue(result.isPresent());
        assertEquals("20123456789", result.get());
        verify(branchRepository).findById(branchId);
        verify(workshopRepository).findById(workshopId);
    }

    @Test
    void handle_GetIssuerTaxIdByBranchId_BranchNotFound_ReturnsEmpty() {
        BranchId branchId = new BranchId(UUID.randomUUID());

        when(branchRepository.findById(branchId)).thenReturn(Optional.empty());

        var result = branchQueryService.handle(new GetIssuerTaxIdByBranchIdQuery(branchId));

        assertTrue(result.isEmpty());
        verify(branchRepository).findById(branchId);
        verify(workshopRepository, never()).findById(any());
    }

    @Test
    void handle_GetIssuerTaxIdByBranchId_WorkshopNotFound_ReturnsEmpty() {
        BranchId branchId = new BranchId(UUID.randomUUID());
        WorkshopId workshopId = new WorkshopId(UUID.randomUUID());
        Branch branch = new Branch(workshopId, "BR-001", "Sede Central", new Address("Av. Central 123"), new Phone("987654321"));

        when(branchRepository.findById(branchId)).thenReturn(Optional.of(branch));
        when(workshopRepository.findById(workshopId)).thenReturn(Optional.empty());

        var result = branchQueryService.handle(new GetIssuerTaxIdByBranchIdQuery(branchId));

        assertTrue(result.isEmpty());
        verify(branchRepository).findById(branchId);
        verify(workshopRepository).findById(workshopId);
    }
}
