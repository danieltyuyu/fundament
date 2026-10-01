package com.forkdevs.driveos.platform.operations.domain.repositories;

import com.forkdevs.driveos.platform.operations.domain.model.aggregates.WorkOrder;
import com.forkdevs.driveos.platform.operations.domain.model.valueobjects.AppointmentId;
import com.forkdevs.driveos.platform.operations.domain.model.valueobjects.WorkOrderId;
import com.forkdevs.driveos.platform.operations.domain.model.valueobjects.WorkOrderTaskId;
import com.forkdevs.driveos.platform.shared.domain.model.valueobjects.BranchId;
import com.forkdevs.driveos.platform.shared.domain.model.valueobjects.CustomerId;
import com.forkdevs.driveos.platform.shared.domain.model.valueobjects.VehicleId;

import java.util.List;
import java.util.Optional;

public interface WorkOrderRepository {

    WorkOrder save(WorkOrder workOrder);

    Optional<WorkOrder> findById(WorkOrderId id);

    Optional<WorkOrder> findByTaskId(WorkOrderTaskId taskId);

    List<WorkOrder> findAllByBranchId(BranchId branchId);

    Optional<WorkOrder> findByAppointmentId(AppointmentId appointmentId);

    boolean existsByAppointmentId(AppointmentId appointmentId);

    List<WorkOrder> findAllByCustomerId(CustomerId customerId);

    List<WorkOrder> findAllByVehicleId(VehicleId vehicleId);

    Optional<WorkOrder> findByInternalNumberAndBranchId(Integer internalNumber, BranchId branchId);

    int findMaxInternalNumberByBranchId(BranchId branchId);
}
