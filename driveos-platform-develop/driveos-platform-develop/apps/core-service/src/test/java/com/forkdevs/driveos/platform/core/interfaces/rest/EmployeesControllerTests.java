package com.forkdevs.driveos.platform.core.interfaces.rest;

import com.forkdevs.driveos.platform.core.application.commandservices.EmployeeCommandService;
import com.forkdevs.driveos.platform.core.application.queryservices.EmployeeQueryService;
import com.forkdevs.driveos.platform.core.domain.model.aggregates.Employee;
import com.forkdevs.driveos.platform.core.domain.model.commands.CreateEmployeeCommand;
import com.forkdevs.driveos.platform.core.domain.model.commands.DeleteEmployeeCommand;
import com.forkdevs.driveos.platform.core.domain.model.commands.UpdateEmployeeCommand;
import com.forkdevs.driveos.platform.core.domain.model.queries.GetEmployeeByDocumentNumberQuery;
import com.forkdevs.driveos.platform.core.domain.model.queries.GetEmployeeByIdQuery;
import com.forkdevs.driveos.platform.core.domain.model.queries.GetEmployeeByUserIdQuery;
import com.forkdevs.driveos.platform.core.domain.model.valueobjects.Document;
import com.forkdevs.driveos.platform.core.domain.model.valueobjects.DocumentType;
import com.forkdevs.driveos.platform.core.domain.model.valueobjects.PersonName;
import com.forkdevs.driveos.platform.core.domain.model.valueobjects.Phone;
import com.forkdevs.driveos.platform.core.domain.model.valueobjects.UserId;
import com.forkdevs.driveos.platform.core.interfaces.rest.resources.CreateEmployeeResource;
import com.forkdevs.driveos.platform.core.interfaces.rest.resources.UpdateEmployeeResource;
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
class EmployeesControllerTests {

    @Mock
    private EmployeeCommandService employeeCommandService;

    @Mock
    private EmployeeQueryService employeeQueryService;

    @InjectMocks
    private EmployeesController employeesController;

    @Test
    void createEmployee_Success_ReturnsCreated() {
        UUID userId = UUID.randomUUID();
        CreateEmployeeResource resource = new CreateEmployeeResource(
                userId, "Ana", "Torres", "DNI", "45678901", "955667788"
        );

        Employee employee = new Employee(
                new UserId(userId), new PersonName("Ana", "Torres"),
                new Document(DocumentType.DNI, "45678901"), new Phone("955667788")
        );

        when(employeeCommandService.handle(any(CreateEmployeeCommand.class))).thenReturn(Optional.of(employee));

        var response = employeesController.createEmployee(resource);

        assertNotNull(response);
        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals("Ana", response.getBody().firstName());
    }

    @Test
    void createEmployee_Failure_ReturnsBadRequest() {
        CreateEmployeeResource resource = new CreateEmployeeResource(
                UUID.randomUUID(), "Ana", "Torres", "DNI", "45678901", "955667788"
        );

        when(employeeCommandService.handle(any(CreateEmployeeCommand.class))).thenReturn(Optional.empty());

        var response = employeesController.createEmployee(resource);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
    }

    @Test
    void updateEmployee_Success_ReturnsOk() {
        UUID employeeId = UUID.randomUUID();
        UpdateEmployeeResource resource = new UpdateEmployeeResource(
                "Ana Maria", "Torres", "DNI", "45678901", "988776655"
        );

        Employee employee = new Employee(
                new UserId(UUID.randomUUID()), new PersonName("Ana Maria", "Torres"),
                new Document(DocumentType.DNI, "45678901"), new Phone("988776655")
        );

        when(employeeCommandService.handle(any(UpdateEmployeeCommand.class))).thenReturn(Optional.of(employee));

        var response = employeesController.updateEmployee(employeeId, resource);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals("Ana Maria", response.getBody().firstName());
    }

    @Test
    void updateEmployee_Failure_ReturnsBadRequest() {
        UUID employeeId = UUID.randomUUID();
        UpdateEmployeeResource resource = new UpdateEmployeeResource(
                "Ana", "Torres", "DNI", "45678901", "955667788"
        );

        when(employeeCommandService.handle(any(UpdateEmployeeCommand.class))).thenReturn(Optional.empty());

        var response = employeesController.updateEmployee(employeeId, resource);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
    }

    @Test
    void getEmployeeById_Found_ReturnsOk() {
        UUID employeeId = UUID.randomUUID();
        Employee employee = new Employee(
                new UserId(UUID.randomUUID()), new PersonName("Ana", "Torres"),
                new Document(DocumentType.DNI, "45678901"), new Phone("955667788")
        );

        when(employeeQueryService.handle(any(GetEmployeeByIdQuery.class))).thenReturn(Optional.of(employee));

        var response = employeesController.getEmployeeById(employeeId);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
    }

    @Test
    void getEmployeeById_NotFound_ReturnsNotFound() {
        UUID employeeId = UUID.randomUUID();

        when(employeeQueryService.handle(any(GetEmployeeByIdQuery.class))).thenReturn(Optional.empty());

        var response = employeesController.getEmployeeById(employeeId);

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
    }

    @Test
    void getEmployeeByUserId_Found_ReturnsOk() {
        UUID userId = UUID.randomUUID();
        Employee employee = new Employee(
                new UserId(userId), new PersonName("Ana", "Torres"),
                new Document(DocumentType.DNI, "45678901"), new Phone("955667788")
        );

        when(employeeQueryService.handle(any(GetEmployeeByUserIdQuery.class))).thenReturn(Optional.of(employee));

        var response = employeesController.getEmployeeByUserId(userId);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
    }

    @Test
    void getEmployeeByUserId_NotFound_ReturnsNotFound() {
        UUID userId = UUID.randomUUID();

        when(employeeQueryService.handle(any(GetEmployeeByUserIdQuery.class))).thenReturn(Optional.empty());

        var response = employeesController.getEmployeeByUserId(userId);

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
    }

    @Test
    void getEmployeeByDocumentNumber_Found_ReturnsOk() {
        String doc = "45678901";
        Employee employee = new Employee(
                new UserId(UUID.randomUUID()), new PersonName("Ana", "Torres"),
                new Document(DocumentType.DNI, doc), new Phone("955667788")
        );

        when(employeeQueryService.handle(any(GetEmployeeByDocumentNumberQuery.class))).thenReturn(Optional.of(employee));

        var response = employeesController.getEmployeeByDocumentNumber(doc);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
    }

    @Test
    void getEmployeeByDocumentNumber_NotFound_ReturnsNotFound() {
        String doc = "45678901";

        when(employeeQueryService.handle(any(GetEmployeeByDocumentNumberQuery.class))).thenReturn(Optional.empty());

        var response = employeesController.getEmployeeByDocumentNumber(doc);

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
    }

    @Test
    void deleteEmployee_Success_ReturnsOk() {
        UUID employeeId = UUID.randomUUID();

        var response = employeesController.deleteEmployee(employeeId);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        verify(employeeCommandService).handle(any(DeleteEmployeeCommand.class));
    }
}
