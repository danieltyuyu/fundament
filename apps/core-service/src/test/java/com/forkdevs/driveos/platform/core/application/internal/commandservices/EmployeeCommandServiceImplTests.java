package com.forkdevs.driveos.platform.core.application.internal.commandservices;

import com.forkdevs.driveos.platform.core.domain.model.aggregates.Employee;
import com.forkdevs.driveos.platform.core.domain.model.commands.CreateEmployeeCommand;
import com.forkdevs.driveos.platform.core.domain.model.commands.DeleteEmployeeCommand;
import com.forkdevs.driveos.platform.core.domain.model.commands.UpdateEmployeeCommand;
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
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class EmployeeCommandServiceImplTests {

    @Mock
    private EmployeeRepository employeeRepository;

    @InjectMocks
    private EmployeeCommandServiceImpl employeeCommandService;

    @Test
    void handle_CreateEmployee_Success() {
        UserId userId = new UserId(UUID.randomUUID());
        PersonName name = new PersonName("Ana", "Torres");
        Document document = new Document(DocumentType.DNI, "45678901");
        Phone phone = new Phone("955667788");

        CreateEmployeeCommand command = new CreateEmployeeCommand(userId, name, document, phone);

        when(employeeRepository.existsByUserId(userId)).thenReturn(false);
        when(employeeRepository.save(any(Employee.class))).thenAnswer(invocation -> invocation.getArgument(0));

        var result = employeeCommandService.handle(command);

        assertTrue(result.isPresent());
        var employee = result.get();
        assertEquals(userId, employee.getUserId());
        assertEquals("Ana Torres", employee.getName().getFullName());
        assertEquals("45678901", employee.getDocument().getDocumentNumber());
        assertEquals("955667788", employee.getPhone().value());
        verify(employeeRepository).existsByUserId(userId);
        verify(employeeRepository).save(any(Employee.class));
    }

    @Test
    void handle_CreateEmployee_ProfileAlreadyExists_ThrowsIllegalArgumentException() {
        UserId userId = new UserId(UUID.randomUUID());
        PersonName name = new PersonName("Ana", "Torres");
        Document document = new Document(DocumentType.DNI, "45678901");
        Phone phone = new Phone("955667788");

        CreateEmployeeCommand command = new CreateEmployeeCommand(userId, name, document, phone);

        when(employeeRepository.existsByUserId(userId)).thenReturn(true);

        var exception = assertThrows(IllegalArgumentException.class, () -> employeeCommandService.handle(command));
        assertEquals("core.error.employee.profileAlreadyExists", exception.getMessage());
        verify(employeeRepository, never()).save(any());
    }

    @Test
    void handle_UpdateEmployee_Success() {
        EmployeeId employeeId = new EmployeeId(UUID.randomUUID());
        UserId userId = new UserId(UUID.randomUUID());
        PersonName originalName = new PersonName("Ana", "Torres");
        Document document = new Document(DocumentType.DNI, "45678901");
        Phone phone = new Phone("955667788");

        Employee existingEmployee = new Employee(userId, originalName, document, phone);

        PersonName updatedName = new PersonName("Ana Maria", "Torres");
        Phone updatedPhone = new Phone("988776655");
        UpdateEmployeeCommand command = new UpdateEmployeeCommand(employeeId, updatedName, document, updatedPhone);

        when(employeeRepository.findById(employeeId)).thenReturn(Optional.of(existingEmployee));
        when(employeeRepository.save(existingEmployee)).thenReturn(existingEmployee);

        var result = employeeCommandService.handle(command);

        assertTrue(result.isPresent());
        assertEquals("Ana Maria Torres", result.get().getName().getFullName());
        assertEquals("988776655", result.get().getPhone().value());
        verify(employeeRepository).save(existingEmployee);
    }

    @Test
    void handle_UpdateEmployee_NotFound_ThrowsIllegalArgumentException() {
        EmployeeId employeeId = new EmployeeId(UUID.randomUUID());
        UpdateEmployeeCommand command = new UpdateEmployeeCommand(
                employeeId,
                new PersonName("Ana", "Torres"),
                new Document(DocumentType.DNI, "45678901"),
                new Phone("955667788")
        );

        when(employeeRepository.findById(employeeId)).thenReturn(Optional.empty());

        var exception = assertThrows(IllegalArgumentException.class, () -> employeeCommandService.handle(command));
        assertEquals("core.error.employee.notFound", exception.getMessage());
        verify(employeeRepository, never()).save(any());
    }

    @Test
    void handle_DeleteEmployee_Success() {
        EmployeeId employeeId = new EmployeeId(UUID.randomUUID());
        Employee existingEmployee = mock(Employee.class);

        when(employeeRepository.findById(employeeId)).thenReturn(Optional.of(existingEmployee));

        employeeCommandService.handle(new DeleteEmployeeCommand(employeeId));

        verify(employeeRepository).delete(existingEmployee);
    }

    @Test
    void handle_DeleteEmployee_NotFound_ThrowsIllegalArgumentException() {
        EmployeeId employeeId = new EmployeeId(UUID.randomUUID());

        when(employeeRepository.findById(employeeId)).thenReturn(Optional.empty());

        var exception = assertThrows(IllegalArgumentException.class, () ->
                employeeCommandService.handle(new DeleteEmployeeCommand(employeeId)));
        assertEquals("core.error.employee.notFound", exception.getMessage());
        verify(employeeRepository, never()).delete(any());
    }
}
