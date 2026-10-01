package com.forkdevs.driveos.platform.core.application.internal.commandservices;

import com.forkdevs.driveos.platform.core.application.commandservices.CustomerCommandService;
import com.forkdevs.driveos.platform.core.domain.model.aggregates.Customer;
import com.forkdevs.driveos.platform.core.domain.model.commands.CreateCustomerCommand;
import com.forkdevs.driveos.platform.core.domain.model.commands.DeleteCustomerCommand;
import com.forkdevs.driveos.platform.core.domain.model.commands.UpdateCustomerCommand;
import com.forkdevs.driveos.platform.core.domain.repositories.CustomerRepository;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class CustomerCommandServiceImpl implements CustomerCommandService {

    private final CustomerRepository customerRepository;

    public CustomerCommandServiceImpl(CustomerRepository customerRepository) {
        this.customerRepository = customerRepository;
    }

    @Override
    public Optional<Customer> handle(CreateCustomerCommand command) {
        if (customerRepository.existsByUserId(command.userId())) {
            throw new IllegalArgumentException("core.error.customer.profileAlreadyExists");
        }

        var customer = new Customer(
                command.userId(),
                command.isCorporate(),
                command.name(),
                command.businessName(),
                command.document(),
                command.phone()
        );

        var savedCustomer = customerRepository.save(customer);
        return Optional.of(savedCustomer);
    }

    @Override
    public Optional<Customer> handle(UpdateCustomerCommand command) {
        var result = customerRepository.findById(command.customerId());
        if (result.isEmpty()) throw new IllegalArgumentException("core.error.customer.notFound");
        
        var customer = result.get();
        
        customer.update(
            command.name(), 
            command.businessName(), 
            command.document(), 
            command.phone()
        );
        
        var savedCustomer = customerRepository.save(customer);
        return Optional.of(savedCustomer);
    }

    @Override
    public void handle(DeleteCustomerCommand command) {
        var existingCustomer = customerRepository.findById(command.customerId());
        if (existingCustomer.isEmpty()) {
            throw new IllegalArgumentException("core.error.customer.notFound");
        }
        
        customerRepository.delete(existingCustomer.get());
    }
}
