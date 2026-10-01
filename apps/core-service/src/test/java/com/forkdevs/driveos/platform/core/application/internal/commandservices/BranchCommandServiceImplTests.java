package com.forkdevs.driveos.platform.core.application.internal.commandservices;

import com.forkdevs.driveos.platform.core.domain.model.aggregates.Branch;
import com.forkdevs.driveos.platform.core.domain.model.commands.CreateBranchCommand;
import com.forkdevs.driveos.platform.core.domain.model.commands.UpdateBranchCommand;
import com.forkdevs.driveos.platform.core.domain.model.valueobjects.Phone;
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

import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class BranchCommandServiceImplTests {

    @Mock
    private BranchRepository branchRepository;

    @Mock
    private WorkshopRepository workshopRepository;

    @InjectMocks
    private BranchCommandServiceImpl branchCommandService;

    @Test
    void handle_CreateBranch_Success() {
        WorkshopId workshopId = new WorkshopId(UUID.randomUUID());
        CreateBranchCommand command = new CreateBranchCommand(
                workshopId,
                "BR-001",
                "Sede Central",
                new Address("Av. Javier Prado 1234"),
                new Phone("987654321")
        );

        when(workshopRepository.existsById(workshopId)).thenReturn(true);
        when(branchRepository.existsByCode("BR-001")).thenReturn(false);
        when(branchRepository.save(any(Branch.class))).thenAnswer(invocation -> invocation.getArgument(0));

        var result = branchCommandService.handle(command);

        assertTrue(result.isPresent());
        var branch = result.get();
        assertEquals(workshopId, branch.getWorkshopId());
        assertEquals("BR-001", branch.getCode());
        assertEquals("Sede Central", branch.getName());
        assertEquals("Av. Javier Prado 1234", branch.getAddress().value());
        assertEquals("987654321", branch.getPhone().value());
        verify(branchRepository).save(any(Branch.class));
    }

    @Test
    void handle_CreateBranch_WorkshopNotFound_ThrowsIllegalArgumentException() {
        WorkshopId workshopId = new WorkshopId(UUID.randomUUID());
        CreateBranchCommand command = new CreateBranchCommand(
                workshopId,
                "BR-001",
                "Sede Central",
                new Address("Av. Javier Prado 1234"),
                new Phone("987654321")
        );

        when(workshopRepository.existsById(workshopId)).thenReturn(false);

        var exception = assertThrows(IllegalArgumentException.class, () -> branchCommandService.handle(command));
        assertEquals("core.error.workshop.notFound", exception.getMessage());
        verify(branchRepository, never()).save(any());
    }

    @Test
    void handle_CreateBranch_CodeMustBeUnique_ThrowsIllegalArgumentException() {
        WorkshopId workshopId = new WorkshopId(UUID.randomUUID());
        CreateBranchCommand command = new CreateBranchCommand(
                workshopId,
                "BR-001",
                "Sede Central",
                new Address("Av. Javier Prado 1234"),
                new Phone("987654321")
        );

        when(workshopRepository.existsById(workshopId)).thenReturn(true);
        when(branchRepository.existsByCode("BR-001")).thenReturn(true);

        var exception = assertThrows(IllegalArgumentException.class, () -> branchCommandService.handle(command));
        assertEquals("core.error.branch.codeMustBeUnique", exception.getMessage());
        verify(branchRepository, never()).save(any());
    }

    @Test
    void handle_UpdateBranch_SameCode_Success() {
        BranchId branchId = new BranchId(UUID.randomUUID());
        WorkshopId workshopId = new WorkshopId(UUID.randomUUID());

        Branch existingBranch = new Branch(
                workshopId,
                "BR-001",
                "Sede Central",
                new Address("Av. Javier Prado 1234"),
                new Phone("987654321")
        );

        UpdateBranchCommand command = new UpdateBranchCommand(
                branchId,
                "BR-001",
                "Sede Central Renovada",
                new Address("Av. La Marina 5678"),
                new Phone("911223344")
        );

        when(branchRepository.findById(branchId)).thenReturn(Optional.of(existingBranch));
        when(branchRepository.save(existingBranch)).thenReturn(existingBranch);

        var result = branchCommandService.handle(command);

        assertTrue(result.isPresent());
        assertEquals("Sede Central Renovada", result.get().getName());
        assertEquals("Av. La Marina 5678", result.get().getAddress().value());
        assertEquals("911223344", result.get().getPhone().value());
        verify(branchRepository, never()).existsByCode(any());
        verify(branchRepository).save(existingBranch);
    }

    @Test
    void handle_UpdateBranch_NewUniqueCode_Success() {
        BranchId branchId = new BranchId(UUID.randomUUID());
        WorkshopId workshopId = new WorkshopId(UUID.randomUUID());

        Branch existingBranch = new Branch(
                workshopId,
                "BR-001",
                "Sede Central",
                new Address("Av. Javier Prado 1234"),
                new Phone("987654321")
        );

        UpdateBranchCommand command = new UpdateBranchCommand(
                branchId,
                "BR-002",
                "Sede Norte",
                new Address("Av. Tupac Amaru 999"),
                new Phone("955443322")
        );

        when(branchRepository.findById(branchId)).thenReturn(Optional.of(existingBranch));
        when(branchRepository.existsByCode("BR-002")).thenReturn(false);
        when(branchRepository.save(existingBranch)).thenReturn(existingBranch);

        var result = branchCommandService.handle(command);

        assertTrue(result.isPresent());
        assertEquals("BR-002", result.get().getCode());
        assertEquals("Sede Norte", result.get().getName());
        verify(branchRepository).existsByCode("BR-002");
        verify(branchRepository).save(existingBranch);
    }

    @Test
    void handle_UpdateBranch_NewCodeAlreadyExists_ThrowsIllegalArgumentException() {
        BranchId branchId = new BranchId(UUID.randomUUID());
        WorkshopId workshopId = new WorkshopId(UUID.randomUUID());

        Branch existingBranch = new Branch(
                workshopId,
                "BR-001",
                "Sede Central",
                new Address("Av. Javier Prado 1234"),
                new Phone("987654321")
        );

        UpdateBranchCommand command = new UpdateBranchCommand(
                branchId,
                "BR-002",
                "Sede Norte",
                new Address("Av. Tupac Amaru 999"),
                new Phone("955443322")
        );

        when(branchRepository.findById(branchId)).thenReturn(Optional.of(existingBranch));
        when(branchRepository.existsByCode("BR-002")).thenReturn(true);

        var exception = assertThrows(IllegalArgumentException.class, () -> branchCommandService.handle(command));
        assertEquals("core.error.branch.codeMustBeUnique", exception.getMessage());
        verify(branchRepository, never()).save(any());
    }

    @Test
    void handle_UpdateBranch_NotFound_ThrowsIllegalArgumentException() {
        BranchId branchId = new BranchId(UUID.randomUUID());
        UpdateBranchCommand command = new UpdateBranchCommand(
                branchId,
                "BR-001",
                "Sede Central",
                new Address("Av. Javier Prado 1234"),
                new Phone("987654321")
        );

        when(branchRepository.findById(branchId)).thenReturn(Optional.empty());

        var exception = assertThrows(IllegalArgumentException.class, () -> branchCommandService.handle(command));
        assertEquals("core.error.branch.notFound", exception.getMessage());
        verify(branchRepository, never()).save(any());
    }
}
