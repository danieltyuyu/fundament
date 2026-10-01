package com.forkdevs.driveos.platform.operations.domain.repositories;

import com.forkdevs.driveos.platform.operations.domain.model.aggregates.Service;
import com.forkdevs.driveos.platform.operations.domain.model.valueobjects.ServiceId;
import com.forkdevs.driveos.platform.shared.domain.model.valueobjects.BranchId;

import java.util.List;
import java.util.Optional;

public interface ServiceRepository {
    Service save(Service service);
    Optional<Service> findById(ServiceId id);
    List<Service> findAllByBranchId(BranchId branchId);
    void delete(Service service);
}
