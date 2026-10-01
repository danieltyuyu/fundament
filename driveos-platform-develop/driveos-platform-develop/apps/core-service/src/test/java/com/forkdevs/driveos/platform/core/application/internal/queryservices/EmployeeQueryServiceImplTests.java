package com.forkdevs.driveos.platform.core.application.internal.queryservices;

import com.forkdevs.driveos.platform.core.domain.model.aggregates.Employee;
import com.forkdevs.driveos.platform.core.domain.model.queries.GetEmployeeByDocumentNumberQuery;
import com.forkdevs.driveos.platform.core.domain.model.queries.GetEmployeeByIdQuery;
import com.forkdevs.driveos.platform.core.domain.model.queries.GetEmployeeByUserIdQuery;
import com.forkdevs.driveos.platform.core.domain.model.valueobjects.Document;
import com.forkdevs.driveos.platform.core.domain.model.valueobjects.DocumentType;
import com.forkdevs.driveos.platform.core.domain.model.valueobjects.EmployeeId;
import com.forkdevs.driveos.platform.core.domain.model.valueobjects.PersonName;
import com.forkdevs.driveos.platform.core.domain.model.valueobjects.Phone;
import com.forkdevs.driveos.platform.core.domain.model.valueobjects.UserId;
import com.forkdevs.driveos.platform.core.domain.repositories.EmployeeRepository;
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
class EmployeeQueryServiceImplTests {

    @Mock
    private EmployeeRepository employeeRepository;

    @InjectMocks
    private EmployeeQueryServiceImpl employeeQueryService;

    @Test
    void handle_GetEmployeeById_Found_ReturnsEmployee() {
        EmployeeId employeeId = new EmployeeId(UUID.randomUUID());
        UserId userId = new UserId(UUID.randomUUID());
        Employee employee = new Employee(
                userId,
                new PersonName("Ana", "Torres"),
                new Document(DocumentType.DNI, "45678901"),
                new Phone("955667788")
        );

        when(employeeRepository.findById(employeeId)).thenReturn(Optional.of(employee));

        var result = employeeQueryService.handle(new GetEmployeeByIdQuery(employeeId));

        assertTrue(result.isPresent());
        assertEquals(userId, result.get().getUserId());
        assertEquals("Ana Torres", result.get().getName().getFullName());
        verify(employeeRepository).findById(employeeId);
    }

    @Test
    void handle_GetEmployeeById_NotFound_ReturnsEmpty() {
        EmployeeId employeeId = new EmployeeId(UUID.randomUUID());

        when(employeeRepository.findById(employeeId)).thenReturn(Optional.empty());

        var result = employeeQueryService.handle(new GetEmployeeByIdQuery(employeeId));

        assertTrue(result.isEmpty());
        verify(employeeRepository).findById(employeeId);
    }

    @Test
    void handle_GetEmployeeByUserId_Found_ReturnsEmployee() {
        UserId userId = new UserId(UUID.randomUUID());
        Employee employee = new Employee(
                userId,
                new PersonName("Ana", "Torres"),
                new Document(DocumentType.DNI, "45678901"),
                new Phone("955667788")
        );

        when(employeeRepository.findByUserId(userId)).thenReturn(Optional.of(employee));

        var result = employeeQueryService.handle(new GetEmployeeByUserIdQuery(userId));

        assertTrue(result.isPresent());
        assertEquals(userId, result.get().getUserId());
        verify(employeeRepository).findByUserId(userId);
    }

    @Test
    void handle_GetEmployeeByUserId_NotFound_ReturnsEmpty() {
        UserId userId = new UserId(UUID.randomUUID());

        when(employeeRepository.findByUserId(userId)).thenReturn(Optional.empty());

        var result = employeeQueryService.handle(new GetEmployeeByUserIdQuery(userId));

        assertTrue(result.isEmpty());
        verify(employeeRepository).findByUserId(userId);
    }

    @Test
    void handle_GetEmployeeByDocumentNumber_Found_ReturnsEmployee() {
        String docNumber = "45678901";
        UserId userId = new UserId(UUID.randomUUID());
        Employee employee = new Employee(
                userId,
                new PersonName("Ana", "Torres"),
                new Document(DocumentType.DNI, docNumber),
                new Phone("955667788")
        );

        when(employeeRepository.findByDocumentNumber(docNumber)).thenReturn(Optional.of(employee));

        var result = employeeQueryService.handle(new GetEmployeeByDocumentNumberQuery(docNumber));

        assertTrue(result.isPresent());
        assertEquals(docNumber, result.get().getDocument().getDocumentNumber());
        verify(employeeRepository).findByDocumentNumber(docNumber);
    }

    @Test
    void handle_GetEmployeeByDocumentNumber_NotFound_ReturnsEmpty() {
        String docNumber = "45678901";

        when(employeeRepository.findByDocumentNumber(docNumber)).thenReturn(Optional.empty());

        var result = employeeQueryService.handle(new GetEmployeeByDocumentNumberQuery(docNumber));

        assertTrue(result.isEmpty());
        verify(employeeRepository).findByDocumentNumber(docNumber);
    }
}
