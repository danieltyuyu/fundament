package com.forkdevs.driveos.platform.core.application.internal.commandservices;

import com.forkdevs.driveos.platform.core.domain.model.aggregates.BranchSubscription;
import com.forkdevs.driveos.platform.core.domain.model.aggregates.SubscriptionPlan;
import com.forkdevs.driveos.platform.core.domain.model.commands.AssignSubscriptionCommand;
import com.forkdevs.driveos.platform.core.domain.model.commands.CancelSubscriptionCommand;
import com.forkdevs.driveos.platform.core.domain.model.valueobjects.BillingCycle;
import com.forkdevs.driveos.platform.core.domain.model.valueobjects.CreditCard;
import com.forkdevs.driveos.platform.core.domain.model.valueobjects.SubscriptionPlanId;
import com.forkdevs.driveos.platform.core.domain.model.valueobjects.SubscriptionStatus;
import com.forkdevs.driveos.platform.core.domain.repositories.BranchRepository;
import com.forkdevs.driveos.platform.core.domain.repositories.BranchSubscriptionRepository;
import com.forkdevs.driveos.platform.core.domain.repositories.SubscriptionPlanRepository;
import com.forkdevs.driveos.platform.shared.domain.model.valueobjects.BranchId;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Date;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class SubscriptionCommandServiceImplTests {

    @Mock
    private BranchSubscriptionRepository subscriptionRepository;

    @Mock
    private SubscriptionPlanRepository planRepository;

    @Mock
    private BranchRepository branchRepository;

    @InjectMocks
    private SubscriptionCommandServiceImpl subscriptionCommandService;

    @Test
    void handle_AssignSubscription_Monthly_NoPriorActive_Success() {
        BranchId branchId = new BranchId(UUID.randomUUID());
        SubscriptionPlanId planId = new SubscriptionPlanId(UUID.randomUUID());
        CreditCard creditCard = new CreditCard("1234567812345678", "Juan Perez", "12/28", "123");

        AssignSubscriptionCommand command = new AssignSubscriptionCommand(
                branchId,
                planId,
                BillingCycle.MONTHLY,
                creditCard
        );

        SubscriptionPlan plan = new SubscriptionPlan("Pro", 99.99, 10, 50, 200, 5, true);

        when(branchRepository.existsById(branchId)).thenReturn(true);
        when(planRepository.findById(planId)).thenReturn(Optional.of(plan));
        when(subscriptionRepository.findActiveByBranchId(branchId)).thenReturn(Optional.empty());
        when(subscriptionRepository.save(any(BranchSubscription.class))).thenAnswer(invocation -> invocation.getArgument(0));

        var result = subscriptionCommandService.handle(command);

        assertTrue(result.isPresent());
        var sub = result.get();
        assertEquals(branchId, sub.getBranchId());
        assertEquals(planId, sub.getPlanId());
        assertEquals(BillingCycle.MONTHLY, sub.getBillingCycle());
        assertEquals(SubscriptionStatus.ACTIVE, sub.getStatus());
        assertNotNull(sub.getStartDate());
        assertNotNull(sub.getEndDate());
        verify(subscriptionRepository).save(any(BranchSubscription.class));
    }

    @Test
    void handle_AssignSubscription_Annual_CancelsExistingActive_Success() {
        BranchId branchId = new BranchId(UUID.randomUUID());
        SubscriptionPlanId planId = new SubscriptionPlanId(UUID.randomUUID());

        AssignSubscriptionCommand command = new AssignSubscriptionCommand(
                branchId,
                planId,
                BillingCycle.ANNUAL,
                null
        );

        SubscriptionPlan plan = new SubscriptionPlan("Enterprise", 299.99, 50, 100, 1000, 20, true);
        BranchSubscription existingActiveSub = new BranchSubscription(
                branchId,
                planId,
                BillingCycle.MONTHLY,
                new Date(),
                new Date()
        );

        when(branchRepository.existsById(branchId)).thenReturn(true);
        when(planRepository.findById(planId)).thenReturn(Optional.of(plan));
        when(subscriptionRepository.findActiveByBranchId(branchId)).thenReturn(Optional.of(existingActiveSub));
        when(subscriptionRepository.save(any(BranchSubscription.class))).thenAnswer(invocation -> invocation.getArgument(0));

        var result = subscriptionCommandService.handle(command);

        assertTrue(result.isPresent());
        assertEquals(BillingCycle.ANNUAL, result.get().getBillingCycle());
        assertEquals(SubscriptionStatus.CANCELED, existingActiveSub.getStatus());
        assertNotNull(existingActiveSub.getCanceledAt());
        verify(subscriptionRepository, times(2)).save(any(BranchSubscription.class));
    }

    @Test
    void handle_AssignSubscription_BranchNotFound_ThrowsIllegalArgumentException() {
        BranchId branchId = new BranchId(UUID.randomUUID());
        SubscriptionPlanId planId = new SubscriptionPlanId(UUID.randomUUID());

        AssignSubscriptionCommand command = new AssignSubscriptionCommand(
                branchId,
                planId,
                BillingCycle.MONTHLY,
                null
        );

        when(branchRepository.existsById(branchId)).thenReturn(false);

        var exception = assertThrows(IllegalArgumentException.class, () -> subscriptionCommandService.handle(command));
        assertEquals("core.error.branch.notFound", exception.getMessage());
        verify(planRepository, never()).findById(any());
        verify(subscriptionRepository, never()).save(any());
    }

    @Test
    void handle_AssignSubscription_PlanNotFound_ThrowsIllegalArgumentException() {
        BranchId branchId = new BranchId(UUID.randomUUID());
        SubscriptionPlanId planId = new SubscriptionPlanId(UUID.randomUUID());

        AssignSubscriptionCommand command = new AssignSubscriptionCommand(
                branchId,
                planId,
                BillingCycle.MONTHLY,
                null
        );

        when(branchRepository.existsById(branchId)).thenReturn(true);
        when(planRepository.findById(planId)).thenReturn(Optional.empty());

        var exception = assertThrows(IllegalArgumentException.class, () -> subscriptionCommandService.handle(command));
        assertEquals("Subscription plan does not exist.", exception.getMessage());
        verify(subscriptionRepository, never()).save(any());
    }

    @Test
    void handle_CancelSubscription_Success() {
        BranchId branchId = new BranchId(UUID.randomUUID());
        SubscriptionPlanId planId = new SubscriptionPlanId(UUID.randomUUID());

        BranchSubscription activeSub = new BranchSubscription(
                branchId,
                planId,
                BillingCycle.MONTHLY,
                new Date(),
                new Date()
        );

        when(subscriptionRepository.findActiveByBranchId(branchId)).thenReturn(Optional.of(activeSub));
        when(subscriptionRepository.save(activeSub)).thenReturn(activeSub);

        var result = subscriptionCommandService.handle(new CancelSubscriptionCommand(branchId));

        assertTrue(result.isPresent());
        assertEquals(SubscriptionStatus.CANCELED, result.get().getStatus());
        assertNotNull(result.get().getCanceledAt());
        verify(subscriptionRepository).save(activeSub);
    }

    @Test
    void handle_CancelSubscription_NoActiveSubscription_ThrowsIllegalArgumentException() {
        BranchId branchId = new BranchId(UUID.randomUUID());

        when(subscriptionRepository.findActiveByBranchId(branchId)).thenReturn(Optional.empty());

        var exception = assertThrows(IllegalArgumentException.class, () ->
                subscriptionCommandService.handle(new CancelSubscriptionCommand(branchId)));
        assertEquals("core.error.branch.noActiveSubscription", exception.getMessage());
        verify(subscriptionRepository, never()).save(any());
    }
}
