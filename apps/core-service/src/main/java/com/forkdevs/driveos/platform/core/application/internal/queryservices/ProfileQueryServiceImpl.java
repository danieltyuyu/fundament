package com.forkdevs.driveos.platform.core.application.internal.queryservices;

import com.forkdevs.driveos.platform.core.application.queryservices.ProfileQueryService;
import com.forkdevs.driveos.platform.core.domain.model.queries.GetProfileRolesByUserIdQuery;
import com.forkdevs.driveos.platform.core.domain.repositories.CustomerRepository;
import com.forkdevs.driveos.platform.core.domain.repositories.EmployeeRepository;
import com.forkdevs.driveos.platform.core.domain.repositories.OwnerRepository;
import org.springframework.stereotype.Service;

import com.forkdevs.driveos.platform.core.domain.model.queries.GetProfileByDocumentNumberQuery;
import com.forkdevs.driveos.platform.core.domain.model.queries.responses.ProfileSummary;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class ProfileQueryServiceImpl implements ProfileQueryService {

    private final CustomerRepository customerRepository;
    private final OwnerRepository ownerRepository;
    private final EmployeeRepository employeeRepository;

    public ProfileQueryServiceImpl(
            CustomerRepository customerRepository,
            OwnerRepository ownerRepository,
            EmployeeRepository employeeRepository) {
        this.customerRepository = customerRepository;
        this.ownerRepository = ownerRepository;
        this.employeeRepository = employeeRepository;
    }

    @Override
    public List<String> handle(GetProfileRolesByUserIdQuery query) {
        List<String> roles = new ArrayList<>();

        if (customerRepository.existsByUserId(query.userId())) {
            roles.add("CUSTOMER");
        }
        if (ownerRepository.existsByUserId(query.userId())) {
            roles.add("OWNER");
        }
        if (employeeRepository.existsByUserId(query.userId())) {
            roles.add("EMPLOYEE");
        }

        return roles;
    }

    @Override
    public Optional<ProfileSummary> handle(GetProfileByDocumentNumberQuery query) {
        var customer = customerRepository.findByDocumentNumber(query.documentNumber());
        if (customer.isPresent()) {
            var c = customer.get();
            String firstName = c.getName() != null ? c.getName().firstName() : c.getBusinessName();
            String lastName = c.getName() != null ? c.getName().lastName() : "";
            return Optional.of(new ProfileSummary(c.getId().value(), c.getUserId().value(), firstName, lastName, c.getDocument().getDocumentType().name(), c.getDocument().getDocumentNumber(), "CUSTOMER"));
        }

        var employee = employeeRepository.findByDocumentNumber(query.documentNumber());
        if (employee.isPresent()) {
            var e = employee.get();
            return Optional.of(new ProfileSummary(e.getId().value(), e.getUserId().value(), e.getName().firstName(), e.getName().lastName(), e.getDocument().getDocumentType().name(), e.getDocument().getDocumentNumber(), "EMPLOYEE"));
        }

        var owner = ownerRepository.findByDocumentNumber(query.documentNumber());
        if (owner.isPresent()) {
            var o = owner.get();
            return Optional.of(new ProfileSummary(o.getId().value(), o.getUserId().value(), o.getName().firstName(), o.getName().lastName(), o.getDocument().getDocumentType().name(), o.getDocument().getDocumentNumber(), "OWNER"));
        }

        return Optional.empty();
    }
}
