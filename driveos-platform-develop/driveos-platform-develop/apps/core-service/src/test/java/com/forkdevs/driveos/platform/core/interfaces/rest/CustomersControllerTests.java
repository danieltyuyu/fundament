package com.forkdevs.driveos.platform.core.interfaces.rest;

import com.forkdevs.driveos.platform.core.application.commandservices.CustomerCommandService;
import com.forkdevs.driveos.platform.core.application.queryservices.CustomerQueryService;
import com.forkdevs.driveos.platform.core.domain.model.aggregates.Customer;
import com.forkdevs.driveos.platform.core.domain.model.commands.CreateCustomerCommand;
import com.forkdevs.driveos.platform.core.domain.model.commands.DeleteCustomerCommand;
import com.forkdevs.driveos.platform.core.domain.model.commands.UpdateCustomerCommand;
import com.forkdevs.driveos.platform.core.domain.model.queries.GetCustomerByIdQuery;
import com.forkdevs.driveos.platform.core.domain.model.queries.GetCustomerByUserIdQuery;
import com.forkdevs.driveos.platform.core.domain.model.valueobjects.Document;
import com.forkdevs.driveos.platform.core.domain.model.valueobjects.DocumentType;
import com.forkdevs.driveos.platform.core.domain.model.valueobjects.PersonName;
import com.forkdevs.driveos.platform.core.domain.model.valueobjects.Phone;
import com.forkdevs.driveos.platform.core.domain.model.valueobjects.UserId;
import com.forkdevs.driveos.platform.core.interfaces.rest.resources.CreateCustomerResource;
import com.forkdevs.driveos.platform.core.interfaces.rest.resources.UpdateCustomerResource;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;

import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CustomersControllerTests {

    @Mock
    private CustomerCommandService customerCommandService;

    @Mock
    private CustomerQueryService customerQueryService;

    @InjectMocks
    private CustomersController customersController;

    @Test
    void createCustomer_Success_ReturnsCreated() {
        UUID userId = UUID.randomUUID();
        CreateCustomerResource resource = new CreateCustomerResource(
                userId, false, "Juan", "Perez", null, "DNI", "12345678", "987654321"
        );

        Customer customer = new Customer(
                new UserId(userId), false, new PersonName("Juan", "Perez"), null,
                new Document(DocumentType.DNI, "12345678"), new Phone("987654321")
        );

        when(customerCommandService.handle(any(CreateCustomerCommand.class))).thenReturn(Optional.of(customer));

        var response = customersController.createCustomer(resource);

        assertNotNull(response);
        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals("Juan", response.getBody().firstName());
    }

    @Test
    void createCustomer_Failure_ReturnsBadRequest() {
        CreateCustomerResource resource = new CreateCustomerResource(
                UUID.randomUUID(), false, "Juan", "Perez", null, "DNI", "12345678", "987654321"
        );

        when(customerCommandService.handle(any(CreateCustomerCommand.class))).thenReturn(Optional.empty());

        var response = customersController.createCustomer(resource);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
    }

    @Test
    void updateCustomer_Success_ReturnsOk() {
        UUID customerId = UUID.randomUUID();
        UpdateCustomerResource resource = new UpdateCustomerResource(
                "Juan Carlos", "Perez", null, "DNI", "12345678", "999888777"
        );

        Customer customer = new Customer(
                new UserId(UUID.randomUUID()), false, new PersonName("Juan Carlos", "Perez"), null,
                new Document(DocumentType.DNI, "12345678"), new Phone("999888777")
        );

        when(customerCommandService.handle(any(UpdateCustomerCommand.class))).thenReturn(Optional.of(customer));

        var response = customersController.updateCustomer(customerId, resource);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals("Juan Carlos", response.getBody().firstName());
    }

    @Test
    void updateCustomer_Failure_ReturnsBadRequest() {
        UUID customerId = UUID.randomUUID();
        UpdateCustomerResource resource = new UpdateCustomerResource(
                "Juan", "Perez", null, "DNI", "12345678", "987654321"
        );

        when(customerCommandService.handle(any(UpdateCustomerCommand.class))).thenReturn(Optional.empty());

        var response = customersController.updateCustomer(customerId, resource);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
    }

    @Test
    void getCustomerById_Found_ReturnsOk() {
        UUID customerId = UUID.randomUUID();
        Customer customer = new Customer(
                new UserId(UUID.randomUUID()), false, new PersonName("Juan", "Perez"), null,
                new Document(DocumentType.DNI, "12345678"), new Phone("987654321")
        );

        when(customerQueryService.handle(any(GetCustomerByIdQuery.class))).thenReturn(Optional.of(customer));

        var response = customersController.getCustomerById(customerId);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
    }

    @Test
    void getCustomerById_NotFound_ReturnsNotFound() {
        UUID customerId = UUID.randomUUID();

        when(customerQueryService.handle(any(GetCustomerByIdQuery.class))).thenReturn(Optional.empty());

        var response = customersController.getCustomerById(customerId);

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
    }

    @Test
    void getCustomerByUserId_Found_ReturnsOk() {
        UUID userId = UUID.randomUUID();
        Customer customer = new Customer(
                new UserId(userId), false, new PersonName("Juan", "Perez"), null,
                new Document(DocumentType.DNI, "12345678"), new Phone("987654321")
        );

        when(customerQueryService.handle(any(GetCustomerByUserIdQuery.class))).thenReturn(Optional.of(customer));

        var response = customersController.getCustomerByUserId(userId);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
    }

    @Test
    void getCustomerByUserId_NotFound_ReturnsNotFound() {
        UUID userId = UUID.randomUUID();

        when(customerQueryService.handle(any(GetCustomerByUserIdQuery.class))).thenReturn(Optional.empty());

        var response = customersController.getCustomerByUserId(userId);

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
    }

    @Test
    void deleteCustomer_Success_ReturnsOk() {
        UUID customerId = UUID.randomUUID();

        var response = customersController.deleteCustomer(customerId);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        verify(customerCommandService).handle(any(DeleteCustomerCommand.class));
    }
}
