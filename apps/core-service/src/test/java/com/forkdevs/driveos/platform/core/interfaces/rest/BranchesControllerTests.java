package com.forkdevs.driveos.platform.core.interfaces.rest;

import com.forkdevs.driveos.platform.core.application.commandservices.BranchCommandService;
import com.forkdevs.driveos.platform.core.application.commandservices.SubscriptionCommandService;
import com.forkdevs.driveos.platform.core.application.queryservices.BranchQueryService;
import com.forkdevs.driveos.platform.core.domain.model.aggregates.Branch;
import com.forkdevs.driveos.platform.core.domain.model.aggregates.BranchSubscription;
import com.forkdevs.driveos.platform.core.domain.model.commands.CreateBranchCommand;
import com.forkdevs.driveos.platform.core.domain.model.commands.UpdateBranchCommand;
import com.forkdevs.driveos.platform.core.domain.model.queries.GetAllBranchesByWorkshopIdQuery;
import com.forkdevs.driveos.platform.core.domain.model.queries.GetBranchByIdQuery;
import com.forkdevs.driveos.platform.core.domain.model.queries.GetIssuerTaxIdByBranchIdQuery;
import com.forkdevs.driveos.platform.core.domain.model.valueobjects.BillingCycle;
import com.forkdevs.driveos.platform.core.domain.model.valueobjects.Phone;
import com.forkdevs.driveos.platform.core.domain.model.valueobjects.SubscriptionPlanId;
import com.forkdevs.driveos.platform.core.domain.model.valueobjects.WorkshopId;
import com.forkdevs.driveos.platform.core.interfaces.rest.resources.AssignSubscriptionResource;
import com.forkdevs.driveos.platform.core.interfaces.rest.resources.CreateBranchResource;
import com.forkdevs.driveos.platform.core.interfaces.rest.resources.UpdateBranchResource;
import com.forkdevs.driveos.platform.shared.domain.model.valueobjects.Address;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;

import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class BranchesControllerTests {

    @Mock
    private BranchCommandService branchCommandService;

    @Mock
    private BranchQueryService branchQueryService;

    @Mock
    private SubscriptionCommandService subscriptionCommandService;

    @InjectMocks
    private BranchesController branchesController;

    @Test
    void createBranch_Success_ReturnsCreated() {
        UUID workshopId = UUID.randomUUID();
        CreateBranchResource resource = new CreateBranchResource(workshopId, "BR-01", "Sede 1", "Av. Principal 100", "987654321");
        Branch branch = new Branch(new WorkshopId(workshopId), "BR-01", "Sede 1", new Address("Av. Principal 100"), new Phone("987654321"));

        when(branchCommandService.handle(any(CreateBranchCommand.class))).thenReturn(Optional.of(branch));

        var response = branchesController.createBranch(resource);

        assertNotNull(response);
        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals("BR-01", response.getBody().code());
    }

    @Test
    void createBranch_Failure_ReturnsBadRequest() {
        CreateBranchResource resource = new CreateBranchResource(UUID.randomUUID(), "BR-01", "Sede 1", "Av. Principal 100", "987654321");

        when(branchCommandService.handle(any(CreateBranchCommand.class))).thenReturn(Optional.empty());

        var response = branchesController.createBranch(resource);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
    }

    @Test
    void updateBranch_Success_ReturnsOk() {
        UUID branchId = UUID.randomUUID();
        UpdateBranchResource resource = new UpdateBranchResource("BR-01", "Sede Renovada", "Av. Principal 200", "911223344");
        Branch branch = new Branch(new WorkshopId(UUID.randomUUID()), "BR-01", "Sede Renovada", new Address("Av. Principal 200"), new Phone("911223344"));

        when(branchCommandService.handle(any(UpdateBranchCommand.class))).thenReturn(Optional.of(branch));

        var response = branchesController.updateBranch(branchId, resource);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals("Sede Renovada", response.getBody().name());
    }

    @Test
    void updateBranch_Failure_ReturnsBadRequest() {
        UUID branchId = UUID.randomUUID();
        UpdateBranchResource resource = new UpdateBranchResource("BR-01", "Sede", "Av. Principal 200", "911223344");

        when(branchCommandService.handle(any(UpdateBranchCommand.class))).thenReturn(Optional.empty());

        var response = branchesController.updateBranch(branchId, resource);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
    }

    @Test
    void getBranchById_Found_ReturnsOk() {
        UUID branchId = UUID.randomUUID();
        Branch branch = new Branch(new WorkshopId(UUID.randomUUID()), "BR-01", "Sede", new Address("Av. Principal 100"), new Phone("987654321"));

        when(branchQueryService.handle(any(GetBranchByIdQuery.class))).thenReturn(Optional.of(branch));

        var response = branchesController.getBranchById(branchId);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
    }

    @Test
    void getBranchById_NotFound_ReturnsNotFound() {
        UUID branchId = UUID.randomUUID();

        when(branchQueryService.handle(any(GetBranchByIdQuery.class))).thenReturn(Optional.empty());

        var response = branchesController.getBranchById(branchId);

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
    }

    @Test
    void getBranchesByWorkshopId_ReturnsOkList() {
        UUID workshopId = UUID.randomUUID();
        Branch branch = new Branch(new WorkshopId(workshopId), "BR-01", "Sede", new Address("Av. Principal 100"), new Phone("987654321"));

        when(branchQueryService.handle(any(GetAllBranchesByWorkshopIdQuery.class))).thenReturn(List.of(branch));

        var response = branchesController.getBranchesByWorkshopId(workshopId);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(1, response.getBody().size());
    }

    @Test
    void assignSubscription_Success_ReturnsCreated() {
        UUID branchId = UUID.randomUUID();
        AssignSubscriptionResource resource = new AssignSubscriptionResource(
                UUID.randomUUID(), "MONTHLY", "1234567812345678", "Juan Perez", "12/28", "123"
        );
        BranchSubscription sub = new BranchSubscription(
                new com.forkdevs.driveos.platform.shared.domain.model.valueobjects.BranchId(branchId),
                new SubscriptionPlanId(UUID.randomUUID()),
                BillingCycle.MONTHLY,
                new Date(),
                new Date()
        );

        when(subscriptionCommandService.handle(any(com.forkdevs.driveos.platform.core.domain.model.commands.AssignSubscriptionCommand.class)))
                .thenReturn(Optional.of(sub));

        var response = branchesController.assignSubscription(branchId, resource);

        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertNotNull(response.getBody());
    }

    @Test
    void assignSubscription_Failure_ReturnsBadRequest() {
        UUID branchId = UUID.randomUUID();
        AssignSubscriptionResource resource = new AssignSubscriptionResource(
                UUID.randomUUID(), "MONTHLY", "1234567812345678", "Juan Perez", "12/28", "123"
        );

        when(subscriptionCommandService.handle(any(com.forkdevs.driveos.platform.core.domain.model.commands.AssignSubscriptionCommand.class)))
                .thenReturn(Optional.empty());

        var response = branchesController.assignSubscription(branchId, resource);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
    }

    @Test
    void cancelSubscription_Success_ReturnsOk() {
        UUID branchId = UUID.randomUUID();
        BranchSubscription sub = new BranchSubscription(
                new com.forkdevs.driveos.platform.shared.domain.model.valueobjects.BranchId(branchId),
                new SubscriptionPlanId(UUID.randomUUID()),
                BillingCycle.MONTHLY,
                new Date(),
                new Date()
        );

        when(subscriptionCommandService.handle(any(com.forkdevs.driveos.platform.core.domain.model.commands.CancelSubscriptionCommand.class)))
                .thenReturn(Optional.of(sub));

        var response = branchesController.cancelSubscription(branchId);

        assertEquals(HttpStatus.OK, response.getStatusCode());
    }

    @Test
    void cancelSubscription_Failure_ReturnsBadRequest() {
        UUID branchId = UUID.randomUUID();

        when(subscriptionCommandService.handle(any(com.forkdevs.driveos.platform.core.domain.model.commands.CancelSubscriptionCommand.class)))
                .thenReturn(Optional.empty());

        var response = branchesController.cancelSubscription(branchId);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
    }

    @Test
    void getIssuerTaxIdByBranchId_Found_ReturnsOk() {
        UUID branchId = UUID.randomUUID();

        when(branchQueryService.handle(any(GetIssuerTaxIdByBranchIdQuery.class))).thenReturn(Optional.of("20123456789"));

        var response = branchesController.getIssuerTaxIdByBranchId(branchId);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(branchId, response.getBody().branchId());
        assertEquals("20123456789", response.getBody().taxId());
    }

    @Test
    void getIssuerTaxIdByBranchId_NotFound_ReturnsNotFound() {
        UUID branchId = UUID.randomUUID();

        when(branchQueryService.handle(any(GetIssuerTaxIdByBranchIdQuery.class))).thenReturn(Optional.empty());

        var response = branchesController.getIssuerTaxIdByBranchId(branchId);

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
    }
}
