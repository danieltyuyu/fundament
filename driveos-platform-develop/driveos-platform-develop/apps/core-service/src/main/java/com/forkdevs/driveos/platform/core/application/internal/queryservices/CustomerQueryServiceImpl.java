package com.forkdevs.driveos.platform.core.application.internal.queryservices;

import com.forkdevs.driveos.platform.core.application.queryservices.CustomerQueryService;
import com.forkdevs.driveos.platform.core.domain.model.aggregates.Customer;
import com.forkdevs.driveos.platform.core.domain.model.queries.GetCustomerByIdQuery;
import com.forkdevs.driveos.platform.core.domain.model.queries.GetCustomerByUserIdQuery;
import com.forkdevs.driveos.platform.core.domain.repositories.CustomerRepository;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class CustomerQueryServiceImpl implements CustomerQueryService {
    private final CustomerRepository customerRepository;

    public CustomerQueryServiceImpl(CustomerRepository customerRepository) {
        this.customerRepository = customerRepository;
    }

    @Override
    public Optional<Customer> handle(GetCustomerByIdQuery query) {
        return customerRepository.findById(query.id());
    }

    @Override
    public Optional<Customer> handle(GetCustomerByUserIdQuery query) {
        return customerRepository.findByUserId(query.userId());
    }
}
