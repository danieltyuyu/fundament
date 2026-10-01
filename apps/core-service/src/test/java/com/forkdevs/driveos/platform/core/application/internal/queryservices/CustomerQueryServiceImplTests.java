package com.forkdevs.driveos.platform.core.application.internal.queryservices;

import com.forkdevs.driveos.platform.core.domain.model.aggregates.Customer;
import com.forkdevs.driveos.platform.core.domain.model.queries.GetCustomerByIdQuery;
import com.forkdevs.driveos.platform.core.domain.model.queries.GetCustomerByUserIdQuery;
import com.forkdevs.driveos.platform.core.domain.model.valueobjects.Document;
import com.forkdevs.driveos.platform.core.domain.model.valueobjects.DocumentType;
import com.forkdevs.driveos.platform.core.domain.model.valueobjects.PersonName;
import com.forkdevs.driveos.platform.core.domain.model.valueobjects.Phone;
import com.forkdevs.driveos.platform.core.domain.model.valueobjects.UserId;
import com.forkdevs.driveos.platform.core.domain.repositories.CustomerRepository;
import com.forkdevs.driveos.platform.shared.domain.model.valueobjects.CustomerId;
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
class CustomerQueryServiceImplTests {

    @Mock
    private CustomerRepository customerRepository;

    @InjectMocks
    private CustomerQueryServiceImpl customerQueryService;

    @Test
    void handle_GetCustomerById_Found_ReturnsCustomer() {
        CustomerId customerId = new CustomerId(UUID.randomUUID());
        UserId userId = new UserId(UUID.randomUUID());
        Customer customer = new Customer(
                userId,
                false,
                new PersonName("Juan", "Perez"),
                null,
                new Document(DocumentType.DNI, "12345678"),
                new Phone("987654321")
        );

        when(customerRepository.findById(customerId)).thenReturn(Optional.of(customer));

        var result = customerQueryService.handle(new GetCustomerByIdQuery(customerId));

        assertTrue(result.isPresent());
        assertEquals(userId, result.get().getUserId());
        assertEquals("Juan Perez", result.get().getName().getFullName());
        verify(customerRepository).findById(customerId);
    }

    @Test
    void handle_GetCustomerById_NotFound_ReturnsEmpty() {
        CustomerId customerId = new CustomerId(UUID.randomUUID());

        when(customerRepository.findById(customerId)).thenReturn(Optional.empty());

        var result = customerQueryService.handle(new GetCustomerByIdQuery(customerId));

        assertTrue(result.isEmpty());
        verify(customerRepository).findById(customerId);
    }

    @Test
    void handle_GetCustomerByUserId_Found_ReturnsCustomer() {
        UserId userId = new UserId(UUID.randomUUID());
        Customer customer = new Customer(
                userId,
                false,
                new PersonName("Juan", "Perez"),
                null,
                new Document(DocumentType.DNI, "12345678"),
                new Phone("987654321")
        );

        when(customerRepository.findByUserId(userId)).thenReturn(Optional.of(customer));

        var result = customerQueryService.handle(new GetCustomerByUserIdQuery(userId));

        assertTrue(result.isPresent());
        assertEquals(userId, result.get().getUserId());
        verify(customerRepository).findByUserId(userId);
    }

    @Test
    void handle_GetCustomerByUserId_NotFound_ReturnsEmpty() {
        UserId userId = new UserId(UUID.randomUUID());

        when(customerRepository.findByUserId(userId)).thenReturn(Optional.empty());

        var result = customerQueryService.handle(new GetCustomerByUserIdQuery(userId));

        assertTrue(result.isEmpty());
        verify(customerRepository).findByUserId(userId);
    }
}
