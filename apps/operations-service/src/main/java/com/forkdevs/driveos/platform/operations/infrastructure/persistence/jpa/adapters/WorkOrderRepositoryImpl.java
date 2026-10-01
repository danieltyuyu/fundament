package com.forkdevs.driveos.platform.operations.infrastructure.persistence.jpa.adapters;

import com.forkdevs.driveos.platform.operations.domain.model.aggregates.WorkOrder;
import com.forkdevs.driveos.platform.operations.domain.model.valueobjects.AppointmentId;
import com.forkdevs.driveos.platform.operations.domain.model.valueobjects.WorkOrderId;
import com.forkdevs.driveos.platform.operations.domain.model.valueobjects.WorkOrderTaskId;
import com.forkdevs.driveos.platform.operations.domain.repositories.WorkOrderRepository;
import com.forkdevs.driveos.platform.operations.infrastructure.persistence.jpa.assemblers.WorkOrderPersistenceAssembler;
import com.forkdevs.driveos.platform.operations.infrastructure.persistence.jpa.entities.WorkOrderPersistenceEntity;
import com.forkdevs.driveos.platform.operations.infrastructure.persistence.jpa.repositories.WorkOrderPersistenceRepository;
import com.forkdevs.driveos.platform.shared.domain.model.valueobjects.BranchId;
import com.forkdevs.driveos.platform.shared.domain.model.valueobjects.CustomerId;
import com.forkdevs.driveos.platform.shared.domain.model.valueobjects.VehicleId;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Repository
public class WorkOrderRepositoryImpl implements WorkOrderRepository {

    private final WorkOrderPersistenceRepository workOrderPersistenceRepository;
    private final ApplicationEventPublisher eventPublisher;

    public WorkOrderRepositoryImpl(WorkOrderPersistenceRepository workOrderPersistenceRepository, ApplicationEventPublisher eventPublisher) {
        this.workOrderPersistenceRepository = workOrderPersistenceRepository;
        this.eventPublisher = eventPublisher;
    }

    @Override
    public WorkOrder save(WorkOrder workOrder) {
        WorkOrderPersistenceEntity entity = WorkOrderPersistenceAssembler.toPersistenceEntity(workOrder);
        WorkOrderPersistenceEntity savedEntity = workOrderPersistenceRepository.save(entity);
        WorkOrder savedWorkOrder = WorkOrderPersistenceAssembler.toDomainEntity(savedEntity);
        workOrder.domainEvents().forEach(eventPublisher::publishEvent);
        workOrder.clearDomainEvents();

        return savedWorkOrder;
    }

    @Override
    public Optional<WorkOrder> findById(WorkOrderId id) {
        return workOrderPersistenceRepository.findById(id.value())
                .map(WorkOrderPersistenceAssembler::toDomainEntity);
    }

    @Override
    public Optional<WorkOrder> findByTaskId(WorkOrderTaskId taskId) {
        return workOrderPersistenceRepository.findByTaskId(taskId.value())
                .map(WorkOrderPersistenceAssembler::toDomainEntity);
    }

    @Override
    public List<WorkOrder> findAllByBranchId(BranchId branchId) {
        return workOrderPersistenceRepository.findAllByBranchId(branchId).stream()
                .map(WorkOrderPersistenceAssembler::toDomainEntity)
                .collect(Collectors.toList());
    }

    @Override
    public Optional<WorkOrder> findByAppointmentId(AppointmentId appointmentId) {
        return workOrderPersistenceRepository.findByAppointmentId(appointmentId)
                .map(WorkOrderPersistenceAssembler::toDomainEntity);
    }

    @Override
    public boolean existsByAppointmentId(AppointmentId appointmentId) {
        return workOrderPersistenceRepository.existsByAppointmentId(appointmentId);
    }

    @Override
    public List<WorkOrder> findAllByCustomerId(CustomerId customerId) {
        return workOrderPersistenceRepository.findAllByCustomerId(customerId).stream()
                .map(WorkOrderPersistenceAssembler::toDomainEntity)
                .collect(Collectors.toList());
    }

    @Override
    public List<WorkOrder> findAllByVehicleId(VehicleId vehicleId) {
        return workOrderPersistenceRepository.findAllByVehicleId(vehicleId).stream()
                .map(WorkOrderPersistenceAssembler::toDomainEntity)
                .collect(Collectors.toList());
    }

    @Override
    public Optional<WorkOrder> findByInternalNumberAndBranchId(Integer internalNumber, BranchId branchId) {
        return workOrderPersistenceRepository.findByInternalNumberAndBranchId(internalNumber, branchId)
                .map(WorkOrderPersistenceAssembler::toDomainEntity);
    }

    @Override
    public int findMaxInternalNumberByBranchId(BranchId branchId) {
        return workOrderPersistenceRepository.findMaxInternalNumberByBranchId(branchId);
    }
}
