package com.forkdevs.driveos.platform.fleet.application.internal.queryservices;

import com.forkdevs.driveos.platform.fleet.application.queryservices.CustomerRegistrationQueryFailure;
import com.forkdevs.driveos.platform.fleet.domain.model.aggregates.CustomerRegistration;
import com.forkdevs.driveos.platform.fleet.domain.model.queries.GetCustomerRegistrationByCustomerIdQuery;
import com.forkdevs.driveos.platform.fleet.domain.model.valueobjects.CustomerRegistrationStatus;
import com.forkdevs.driveos.platform.fleet.domain.repositories.CustomerRegistrationRepository;
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
class CustomerRegistrationQueryServiceImplTests {

    @Mock
    private CustomerRegistrationRepository repository;

    @InjectMocks
    private CustomerRegistrationQueryServiceImpl queryService;

    @Test
    void handle_ByBranchId_ReturnsSuccessList() {
        BranchId branchId = new BranchId(UUID.randomUUID());
        CustomerRegistration registration = mock(CustomerRegistration.class);
        when(repository.findByBranchIdAndStatus(branchId, CustomerRegistrationStatus.ACTIVE.value()))
                .thenReturn(List.of(registration));

        var result = queryService.handle(branchId);

        assertTrue(result.isSuccess());
        assertTrue(result.success().isPresent());
        assertEquals(1, result.success().get().size());
    }

    @Test
    void handle_ByRegistrationId_NotFound_ReturnsFailure() {
        UUID registrationId = UUID.randomUUID();
        when(repository.findById(registrationId)).thenReturn(Optional.empty());

        var result = queryService.handle(registrationId);

        assertTrue(result.isFailure());
        assertTrue(result.failure().isPresent());
        assertEquals(CustomerRegistrationQueryFailure.REGISTRATION_NOT_FOUND, result.failure().get());
    }

    @Test
    void handle_ByCustomerIdQuery_Success() {
        UUID customerId = UUID.randomUUID();
        GetCustomerRegistrationByCustomerIdQuery query = new GetCustomerRegistrationByCustomerIdQuery(customerId);
        CustomerRegistration registration = mock(CustomerRegistration.class);

        when(repository.findByCustomerId(customerId)).thenReturn(Optional.of(registration));

        var result = queryService.handle(query);

        assertTrue(result.isSuccess());
        assertTrue(result.success().isPresent());
        assertEquals(registration, result.success().get());
    }
}
