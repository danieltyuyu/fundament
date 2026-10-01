package com.forkdevs.driveos.platform.core.domain.repositories;

import com.forkdevs.driveos.platform.core.domain.model.aggregates.Customer;
import com.forkdevs.driveos.platform.shared.domain.model.valueobjects.CustomerId;
import com.forkdevs.driveos.platform.core.domain.model.valueobjects.UserId;

import java.util.Optional;

public interface CustomerRepository {
    Customer save(Customer customer);
    Optional<Customer> findById(CustomerId id);
    Optional<Customer> findByUserId(UserId userId);
    boolean existsByUserId(UserId userId);
    Optional<Customer> findByDocumentNumber(String documentNumber);
    void delete(Customer customer);
}

