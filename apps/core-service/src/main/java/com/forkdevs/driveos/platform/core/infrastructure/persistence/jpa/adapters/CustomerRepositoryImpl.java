package com.forkdevs.driveos.platform.core.infrastructure.persistence.jpa.adapters;

import com.forkdevs.driveos.platform.core.domain.model.aggregates.Customer;
import com.forkdevs.driveos.platform.shared.domain.model.valueobjects.CustomerId;
import com.forkdevs.driveos.platform.core.domain.model.valueobjects.UserId;
import com.forkdevs.driveos.platform.core.domain.repositories.CustomerRepository;
import com.forkdevs.driveos.platform.core.infrastructure.persistence.jpa.assemblers.CustomerPersistenceAssembler;
import com.forkdevs.driveos.platform.core.infrastructure.persistence.jpa.entities.CustomerPersistenceEntity;
import com.forkdevs.driveos.platform.core.infrastructure.persistence.jpa.repositories.CustomerPersistenceRepository;

import org.springframework.stereotype.Repository;
import java.util.Optional;

@Repository
public class CustomerRepositoryImpl implements CustomerRepository {

    private final CustomerPersistenceRepository customerPersistenceRepository;

    public CustomerRepositoryImpl(CustomerPersistenceRepository customerPersistenceRepository) {
        this.customerPersistenceRepository = customerPersistenceRepository;
    }

    @Override
    public Customer save(Customer customer) {
        CustomerPersistenceEntity entity;
        if (customer.getId() != null) {
            entity = customerPersistenceRepository.findById(customer.getId().value()).orElse(new CustomerPersistenceEntity());
        } else {
            entity = new CustomerPersistenceEntity();
        }
        
        CustomerPersistenceAssembler.toEntity(customer, entity);
        CustomerPersistenceEntity savedEntity = customerPersistenceRepository.save(entity);
        return CustomerPersistenceAssembler.toDomain(savedEntity);
    }

    @Override
    public Optional<Customer> findById(CustomerId id) {
        return customerPersistenceRepository.findById(id.value()).map(CustomerPersistenceAssembler::toDomain);
    }

    @Override
    public Optional<Customer> findByUserId(UserId userId) {
        return customerPersistenceRepository.findByUserId(userId.value()).map(CustomerPersistenceAssembler::toDomain);
    }

    @Override
    public boolean existsByUserId(UserId userId) {
        return customerPersistenceRepository.existsByUserId(userId.value());
    }

    @Override
    public Optional<Customer> findByDocumentNumber(String documentNumber) {
        return customerPersistenceRepository.findByDocumentNumber(documentNumber).map(CustomerPersistenceAssembler::toDomain);
    }

    @Override
    public void delete(Customer customer) {
        if (customer.getId() != null) {
            customerPersistenceRepository.findById(customer.getId().value()).ifPresent(customerPersistenceRepository::delete);
        }
    }
}

