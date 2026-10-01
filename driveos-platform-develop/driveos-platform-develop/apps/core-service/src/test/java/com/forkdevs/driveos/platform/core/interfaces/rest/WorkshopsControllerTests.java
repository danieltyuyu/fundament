package com.forkdevs.driveos.platform.core.interfaces.rest;

import com.forkdevs.driveos.platform.core.application.commandservices.WorkshopCommandService;
import com.forkdevs.driveos.platform.core.application.queryservices.WorkshopQueryService;
import com.forkdevs.driveos.platform.core.domain.model.aggregates.Workshop;
import com.forkdevs.driveos.platform.core.domain.model.commands.CreateWorkshopCommand;
import com.forkdevs.driveos.platform.core.domain.model.commands.UpdateWorkshopCommand;
import com.forkdevs.driveos.platform.core.domain.model.queries.GetAllWorkshopsByOwnerIdQuery;
import com.forkdevs.driveos.platform.core.domain.model.queries.GetWorkshopByIdQuery;
import com.forkdevs.driveos.platform.core.domain.model.valueobjects.OwnerId;
import com.forkdevs.driveos.platform.core.domain.model.valueobjects.TaxId;
import com.forkdevs.driveos.platform.core.interfaces.rest.resources.CreateWorkshopResource;
import com.forkdevs.driveos.platform.core.interfaces.rest.resources.UpdateWorkshopResource;
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
class WorkshopsControllerTests {

    @Mock
    private WorkshopCommandService workshopCommandService;

    @Mock
    private WorkshopQueryService workshopQueryService;

    @InjectMocks
    private WorkshopsController workshopsController;

    @Test
    void createWorkshop_Success_ReturnsCreated() {
        UUID ownerId = UUID.randomUUID();
        CreateWorkshopResource resource = new CreateWorkshopResource(
                ownerId, "Taller SAC", "MecaniPro", "20123456789", 5000
        );

        Workshop workshop = new Workshop(new OwnerId(ownerId), "Taller SAC", "MecaniPro", new TaxId("20123456789"), 5000);

        when(workshopCommandService.handle(any(CreateWorkshopCommand.class))).thenReturn(Optional.of(workshop));

        var response = workshopsController.createWorkshop(resource);

        assertNotNull(response);
        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals("Taller SAC", response.getBody().businessName());
    }

    @Test
    void createWorkshop_Failure_ReturnsBadRequest() {
        CreateWorkshopResource resource = new CreateWorkshopResource(
                UUID.randomUUID(), "Taller SAC", "MecaniPro", "20123456789", 5000
        );

        when(workshopCommandService.handle(any(CreateWorkshopCommand.class))).thenReturn(Optional.empty());

        var response = workshopsController.createWorkshop(resource);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
    }

    @Test
    void updateWorkshop_Success_ReturnsOk() {
        UUID workshopId = UUID.randomUUID();
        UpdateWorkshopResource resource = new UpdateWorkshopResource(
                "Taller Renovado SAC", "NuevoPro", "20987654321", 10000
        );

        Workshop workshop = new Workshop(new OwnerId(UUID.randomUUID()), "Taller Renovado SAC", "NuevoPro", new TaxId("20987654321"), 10000);

        when(workshopCommandService.handle(any(UpdateWorkshopCommand.class))).thenReturn(Optional.of(workshop));

        var response = workshopsController.updateWorkshop(workshopId, resource);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals("Taller Renovado SAC", response.getBody().businessName());
    }

    @Test
    void updateWorkshop_Failure_ReturnsBadRequest() {
        UUID workshopId = UUID.randomUUID();
        UpdateWorkshopResource resource = new UpdateWorkshopResource(
                "Taller SAC", "Pro", "20123456789", 5000
        );

        when(workshopCommandService.handle(any(UpdateWorkshopCommand.class))).thenReturn(Optional.empty());

        var response = workshopsController.updateWorkshop(workshopId, resource);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
    }

    @Test
    void getWorkshopById_Found_ReturnsOk() {
        UUID workshopId = UUID.randomUUID();
        Workshop workshop = new Workshop(new OwnerId(UUID.randomUUID()), "Taller SAC", "MecaniPro", new TaxId("20123456789"), 5000);

        when(workshopQueryService.handle(any(GetWorkshopByIdQuery.class))).thenReturn(Optional.of(workshop));

        var response = workshopsController.getWorkshopById(workshopId);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
    }

    @Test
    void getWorkshopById_NotFound_ReturnsNotFound() {
        UUID workshopId = UUID.randomUUID();

        when(workshopQueryService.handle(any(GetWorkshopByIdQuery.class))).thenReturn(Optional.empty());

        var response = workshopsController.getWorkshopById(workshopId);

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
    }

    @Test
    void getWorkshopsByOwnerId_ReturnsOkList() {
        UUID ownerId = UUID.randomUUID();
        Workshop workshop = new Workshop(new OwnerId(ownerId), "Taller SAC", "MecaniPro", new TaxId("20123456789"), 5000);

        when(workshopQueryService.handle(any(GetAllWorkshopsByOwnerIdQuery.class))).thenReturn(List.of(workshop));

        var response = workshopsController.getWorkshopsByOwnerId(ownerId);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(1, response.getBody().size());
    }
}
