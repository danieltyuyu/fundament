package com.forkdevs.driveos.platform.core.application.internal.queryservices;

import com.forkdevs.driveos.platform.core.domain.model.aggregates.Customer;
import com.forkdevs.driveos.platform.core.domain.model.aggregates.Employee;
import com.forkdevs.driveos.platform.core.domain.model.aggregates.Owner;
import com.forkdevs.driveos.platform.core.domain.model.queries.GetProfileByDocumentNumberQuery;
import com.forkdevs.driveos.platform.core.domain.model.queries.GetProfileRolesByUserIdQuery;
import com.forkdevs.driveos.platform.core.domain.model.valueobjects.Document;
import com.forkdevs.driveos.platform.core.domain.model.valueobjects.DocumentType;
import com.forkdevs.driveos.platform.core.domain.model.valueobjects.PersonName;
import com.forkdevs.driveos.platform.core.domain.model.valueobjects.Phone;
import com.forkdevs.driveos.platform.core.domain.model.valueobjects.UserId;
import com.forkdevs.driveos.platform.core.domain.repositories.CustomerRepository;
import com.forkdevs.driveos.platform.core.domain.repositories.EmployeeRepository;
import com.forkdevs.driveos.platform.core.domain.repositories.OwnerRepository;
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
class ProfileQueryServiceImplTests {

    @Mock
    private CustomerRepository customerRepository;

    @Mock
    private OwnerRepository ownerRepository;

    @Mock
    private EmployeeRepository employeeRepository;

    @InjectMocks
    private ProfileQueryServiceImpl profileQueryService;

    @Test
    void handle_GetProfileRolesByUserId_MultipleRoles_ReturnsAllMatchingRoles() {
        UserId userId = new UserId(UUID.randomUUID());

        when(customerRepository.existsByUserId(userId)).thenReturn(true);
        when(ownerRepository.existsByUserId(userId)).thenReturn(false);
        when(employeeRepository.existsByUserId(userId)).thenReturn(true);

        var roles = profileQueryService.handle(new GetProfileRolesByUserIdQuery(userId));

        assertEquals(2, roles.size());
        assertTrue(roles.contains("CUSTOMER"));
        assertTrue(roles.contains("EMPLOYEE"));
        assertFalse(roles.contains("OWNER"));
    }

    @Test
    void handle_GetProfileRolesByUserId_NoRoles_ReturnsEmptyList() {
        UserId userId = new UserId(UUID.randomUUID());

        when(customerRepository.existsByUserId(userId)).thenReturn(false);
        when(ownerRepository.existsByUserId(userId)).thenReturn(false);
        when(employeeRepository.existsByUserId(userId)).thenReturn(false);

        var roles = profileQueryService.handle(new GetProfileRolesByUserIdQuery(userId));

        assertTrue(roles.isEmpty());
    }

    @Test
    void handle_GetProfileByDocumentNumber_CustomerIndividual_ReturnsCustomerSummary() {
        String doc = "12345678";
        Customer customer = new Customer(
                new UserId(UUID.randomUUID()),
                false,
                new PersonName("Juan", "Perez"),
                null,
                new Document(DocumentType.DNI, doc),
                new Phone("987654321")
        );

        when(customerRepository.findByDocumentNumber(doc)).thenReturn(Optional.of(customer));

        var result = profileQueryService.handle(new GetProfileByDocumentNumberQuery(doc));

        assertTrue(result.isPresent());
        var summary = result.get();
        assertEquals("Juan", summary.firstName());
        assertEquals("Perez", summary.lastName());
        assertEquals("CUSTOMER", summary.profileType());
        assertEquals(doc, summary.documentNumber());
        verify(customerRepository).findByDocumentNumber(doc);
        verify(employeeRepository, never()).findByDocumentNumber(any());
    }

    @Test
    void handle_GetProfileByDocumentNumber_CustomerCorporate_ReturnsCorporateCustomerSummary() {
        String doc = "20123456789";
        Customer customer = new Customer(
                new UserId(UUID.randomUUID()),
                true,
                null,
                "Transportes Express SAC",
                new Document(DocumentType.RUC, doc),
                new Phone("987654321")
        );

        when(customerRepository.findByDocumentNumber(doc)).thenReturn(Optional.of(customer));

        var result = profileQueryService.handle(new GetProfileByDocumentNumberQuery(doc));

        assertTrue(result.isPresent());
        var summary = result.get();
        assertEquals("Transportes Express SAC", summary.firstName());
        assertEquals("", summary.lastName());
        assertEquals("CUSTOMER", summary.profileType());
        assertEquals(doc, summary.documentNumber());
    }

    @Test
    void handle_GetProfileByDocumentNumber_Employee_ReturnsEmployeeSummary() {
        String doc = "45678901";
        Employee employee = new Employee(
                new UserId(UUID.randomUUID()),
                new PersonName("Ana", "Torres"),
                new Document(DocumentType.DNI, doc),
                new Phone("955667788")
        );

        when(customerRepository.findByDocumentNumber(doc)).thenReturn(Optional.empty());
        when(employeeRepository.findByDocumentNumber(doc)).thenReturn(Optional.of(employee));

        var result = profileQueryService.handle(new GetProfileByDocumentNumberQuery(doc));

        assertTrue(result.isPresent());
        var summary = result.get();
        assertEquals("Ana", summary.firstName());
        assertEquals("Torres", summary.lastName());
        assertEquals("EMPLOYEE", summary.profileType());
        verify(ownerRepository, never()).findByDocumentNumber(any());
    }

    @Test
    void handle_GetProfileByDocumentNumber_Owner_ReturnsOwnerSummary() {
        String doc = "87654321";
        Owner owner = new Owner(
                new UserId(UUID.randomUUID()),
                new PersonName("Carlos", "Gomez"),
                new Document(DocumentType.DNI, doc),
                new Phone("912345678")
        );

        when(customerRepository.findByDocumentNumber(doc)).thenReturn(Optional.empty());
        when(employeeRepository.findByDocumentNumber(doc)).thenReturn(Optional.empty());
        when(ownerRepository.findByDocumentNumber(doc)).thenReturn(Optional.of(owner));

        var result = profileQueryService.handle(new GetProfileByDocumentNumberQuery(doc));

        assertTrue(result.isPresent());
        var summary = result.get();
        assertEquals("Carlos", summary.firstName());
        assertEquals("Gomez", summary.lastName());
        assertEquals("OWNER", summary.profileType());
    }

    @Test
    void handle_GetProfileByDocumentNumber_NotFound_ReturnsEmpty() {
        String doc = "99999999";

        when(customerRepository.findByDocumentNumber(doc)).thenReturn(Optional.empty());
        when(employeeRepository.findByDocumentNumber(doc)).thenReturn(Optional.empty());
        when(ownerRepository.findByDocumentNumber(doc)).thenReturn(Optional.empty());

        var result = profileQueryService.handle(new GetProfileByDocumentNumberQuery(doc));

        assertTrue(result.isEmpty());
    }
}
