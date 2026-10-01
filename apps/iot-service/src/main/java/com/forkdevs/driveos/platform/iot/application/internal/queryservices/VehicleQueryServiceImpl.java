package com.forkdevs.driveos.platform.iot.application.internal.queryservices;

import com.forkdevs.driveos.platform.iot.application.queryservices.VehicleQueryService;
import com.forkdevs.driveos.platform.iot.domain.model.aggregates.Vehicle;
import com.forkdevs.driveos.platform.iot.domain.model.queries.GetActiveVehiclesByCustomerIdQuery;
import com.forkdevs.driveos.platform.iot.domain.model.queries.GetVehiclesAvailableForLinkingQuery;
import com.forkdevs.driveos.platform.iot.domain.model.queries.GetVehicleByIdQuery;
import com.forkdevs.driveos.platform.iot.domain.repositories.VehicleRegistrationRepository;
import com.forkdevs.driveos.platform.iot.domain.repositories.VehicleRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

/**
 * Service implementation for handling Vehicle queries inside the iot context.
 */
@Service
public class VehicleQueryServiceImpl implements VehicleQueryService {

    private final VehicleRepository vehicleRepository;
    private final VehicleRegistrationRepository vehicleRegistrationRepository;

    public VehicleQueryServiceImpl(
            VehicleRepository vehicleRepository,
            VehicleRegistrationRepository vehicleRegistrationRepository
    ) {
        this.vehicleRepository = vehicleRepository;
        this.vehicleRegistrationRepository = vehicleRegistrationRepository;
    }

    @Override
    @Transactional(readOnly = true)
    public List<Vehicle> handle(GetVehiclesAvailableForLinkingQuery query) {
        return vehicleRepository.findAvailableForLinkingByBranchId(query.branchId());
    }

    @Override
    @Transactional(readOnly = true)
    public List<Vehicle> handle(GetActiveVehiclesByCustomerIdQuery query) {
        var activeRegistrations = vehicleRegistrationRepository.findAllActiveByUserId(query.customerId().value());
        return activeRegistrations.stream()
                .map(reg -> vehicleRepository.findById(reg.getVehicleId()))
                .filter(Optional::isPresent)
                .map(Optional::get)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<Vehicle> handle(GetVehicleByIdQuery query) {
        return vehicleRepository.findById(query.vehicleId());
    }
}
