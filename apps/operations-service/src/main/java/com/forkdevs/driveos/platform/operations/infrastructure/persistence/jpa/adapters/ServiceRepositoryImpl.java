package com.forkdevs.driveos.platform.operations.infrastructure.persistence.jpa.adapters;

import com.forkdevs.driveos.platform.operations.domain.model.aggregates.Service;
import com.forkdevs.driveos.platform.operations.domain.model.valueobjects.ServiceId;
import com.forkdevs.driveos.platform.operations.domain.repositories.ServiceRepository;
import com.forkdevs.driveos.platform.operations.infrastructure.persistence.jpa.assemblers.ServicePersistenceAssembler;
import com.forkdevs.driveos.platform.operations.infrastructure.persistence.jpa.entities.ServicePersistenceEntity;
import com.forkdevs.driveos.platform.operations.infrastructure.persistence.jpa.repositories.ServicePersistenceRepository;
import com.forkdevs.driveos.platform.shared.domain.model.valueobjects.BranchId;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Repository
public class ServiceRepositoryImpl implements ServiceRepository {

    private final ServicePersistenceRepository servicePersistenceRepository;

    public ServiceRepositoryImpl(ServicePersistenceRepository servicePersistenceRepository) {
        this.servicePersistenceRepository = servicePersistenceRepository;
    }

    @Override
    public Service save(Service service) {
        ServicePersistenceEntity entity;
        if (service.getId() != null) {
            entity = servicePersistenceRepository.findById(service.getId().value()).orElse(new ServicePersistenceEntity());
        } else {
            entity = new ServicePersistenceEntity();
        }

        ServicePersistenceAssembler.toEntity(service, entity);
        ServicePersistenceEntity savedEntity = servicePersistenceRepository.save(entity);
        return ServicePersistenceAssembler.toDomain(savedEntity);
    }

    @Override
    public Optional<Service> findById(ServiceId id) {
        return servicePersistenceRepository.findById(id.value()).map(ServicePersistenceAssembler::toDomain);
    }

    @Override
    public List<Service> findAllByBranchId(BranchId branchId) {
        return servicePersistenceRepository.findAllByBranchId(branchId.value()).stream()
                .map(ServicePersistenceAssembler::toDomain)
                .collect(Collectors.toList());
    }

    @Override
    public void delete(Service service) {
        if (service.getId() != null) {
            servicePersistenceRepository.findById(service.getId().value()).ifPresent(servicePersistenceRepository::delete);
        }
    }
}

