package com.forkdevs.driveos.platform.core.application.internal.commandservices;

import com.forkdevs.driveos.platform.core.domain.model.aggregates.Customer;
import com.forkdevs.driveos.platform.core.domain.model.commands.CreateCustomerCommand;
import com.forkdevs.driveos.platform.core.domain.model.commands.DeleteCustomerCommand;
import com.forkdevs.driveos.platform.core.domain.model.commands.UpdateCustomerCommand;
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
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CustomerCommandServiceImplTests {

    @Mock
    private CustomerRepository customerRepository;

    @InjectMocks
    private CustomerCommandServiceImpl customerCommandService;

    @Test
    void handle_CreateCustomer_Individual_Success() {
        UserId userId = new UserId(UUID.randomUUID());
        PersonName name = new PersonName("Juan", "Perez");
        Document document = new Document(DocumentType.DNI, "12345678");
        Phone phone = new Phone("987654321");

        CreateCustomerCommand command = new CreateCustomerCommand(
                userId,
                false,
                name,
                null,
                document,
                phone
        );

        when(customerRepository.existsByUserId(userId)).thenReturn(false);
        when(customerRepository.save(any(Customer.class))).thenAnswer(invocation -> invocation.getArgument(0));

        var result = customerCommandService.handle(command);

        assertTrue(result.isPresent());
        var customer = result.get();
        assertEquals(userId, customer.getUserId());
        assertFalse(customer.isCorporate());
        assertEquals("Juan Perez", customer.getName().getFullName());
        assertEquals("12345678", customer.getDocument().getDocumentNumber());
        assertEquals("987654321", customer.getPhone().value());
        verify(customerRepository).existsByUserId(userId);
        verify(customerRepository).save(any(Customer.class));
    }

    @Test
    void handle_CreateCustomer_Corporate_Success() {
        UserId userId = new UserId(UUID.randomUUID());
        Document document = new Document(DocumentType.RUC, "20123456789");
        Phone phone = new Phone("987654321");

        CreateCustomerCommand command = new CreateCustomerCommand(
                userId,
                true,
                null,
                "Transportes Express SAC",
                document,
                phone
        );

        when(customerRepository.existsByUserId(userId)).thenReturn(false);
        when(customerRepository.save(any(Customer.class))).thenAnswer(invocation -> invocation.getArgument(0));

        var result = customerCommandService.handle(command);

        assertTrue(result.isPresent());
        var customer = result.get();
        assertEquals(userId, customer.getUserId());
        assertTrue(customer.isCorporate());
        assertEquals("Transportes Express SAC", customer.getBusinessName());
        assertEquals("20123456789", customer.getDocument().getDocumentNumber());
        verify(customerRepository).save(any(Customer.class));
    }

    @Test
    void handle_CreateCustomer_ProfileAlreadyExists_ThrowsIllegalArgumentException() {
        UserId userId = new UserId(UUID.randomUUID());
        PersonName name = new PersonName("Juan", "Perez");
        Document document = new Document(DocumentType.DNI, "12345678");
        Phone phone = new Phone("987654321");

        CreateCustomerCommand command = new CreateCustomerCommand(
                userId,
                false,
                name,
                null,
                document,
                phone
        );

        when(customerRepository.existsByUserId(userId)).thenReturn(true);

        var exception = assertThrows(IllegalArgumentException.class, () -> customerCommandService.handle(command));
        assertEquals("core.error.customer.profileAlreadyExists", exception.getMessage());
        verify(customerRepository, never()).save(any());
    }

    @Test
    void handle_UpdateCustomer_Success() {
        CustomerId customerId = new CustomerId(UUID.randomUUID());
        UserId userId = new UserId(UUID.randomUUID());
        PersonName originalName = new PersonName("Juan", "Perez");
        Document document = new Document(DocumentType.DNI, "12345678");
        Phone phone = new Phone("987654321");

        Customer existingCustomer = new Customer(userId, false, originalName, null, document, phone);

        PersonName updatedName = new PersonName("Juan Carlos", "Perez");
        Phone updatedPhone = new Phone("999888777");
        UpdateCustomerCommand command = new UpdateCustomerCommand(
                customerId,
                updatedName,
                null,
                document,
                updatedPhone
        );

        when(customerRepository.findById(customerId)).thenReturn(Optional.of(existingCustomer));
        when(customerRepository.save(existingCustomer)).thenReturn(existingCustomer);

        var result = customerCommandService.handle(command);

        assertTrue(result.isPresent());
        assertEquals("Juan Carlos Perez", result.get().getName().getFullName());
        assertEquals("999888777", result.get().getPhone().value());
        verify(customerRepository).save(existingCustomer);
    }

    @Test
    void handle_UpdateCustomer_NotFound_ThrowsIllegalArgumentException() {
        CustomerId customerId = new CustomerId(UUID.randomUUID());
        UpdateCustomerCommand command = new UpdateCustomerCommand(
                customerId,
                new PersonName("Juan", "Perez"),
                null,
                new Document(DocumentType.DNI, "12345678"),
                new Phone("987654321")
        );

        when(customerRepository.findById(customerId)).thenReturn(Optional.empty());

        var exception = assertThrows(IllegalArgumentException.class, () -> customerCommandService.handle(command));
        assertEquals("core.error.customer.notFound", exception.getMessage());
        verify(customerRepository, never()).save(any());
    }

    @Test
    void handle_DeleteCustomer_Success() {
        CustomerId customerId = new CustomerId(UUID.randomUUID());
        Customer existingCustomer = mock(Customer.class);

        when(customerRepository.findById(customerId)).thenReturn(Optional.of(existingCustomer));

        customerCommandService.handle(new DeleteCustomerCommand(customerId));

        verify(customerRepository).delete(existingCustomer);
    }

    @Test
    void handle_DeleteCustomer_NotFound_ThrowsIllegalArgumentException() {
        CustomerId customerId = new CustomerId(UUID.randomUUID());

        when(customerRepository.findById(customerId)).thenReturn(Optional.empty());

        var exception = assertThrows(IllegalArgumentException.class, () ->
                customerCommandService.handle(new DeleteCustomerCommand(customerId)));
        assertEquals("core.error.customer.notFound", exception.getMessage());
        verify(customerRepository, never()).delete(any());
    }
}
