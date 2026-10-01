package com.forkdevs.driveos.platform.iot.infrastructure.persistence.jpa.repositories;

import com.forkdevs.driveos.platform.iot.infrastructure.persistence.jpa.entities.VehiclePersistenceEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * Spring Data JPA Repository for "vehicles" mapping native SQL query for available vehicle fleet.
 */
@Repository
public interface VehiclePersistenceRepository extends JpaRepository<VehiclePersistenceEntity, UUID> {

    @Query(value = "SELECT v.* FROM vehicles v " +
                   "JOIN vehicle_registrations vr ON v.id = vr.vehicle_id " +
                   "JOIN customers c ON vr.user_id = c.user_id " +
                   "JOIN customer_registrations cr ON c.id = cr.customer_id " +
                   "WHERE cr.branch_id = :branchId " +
                   "  AND cr.status = 'ACTIVE' " +
                   "  AND vr.status = 'ACTIVE' " +
                   "  AND v.id NOT IN (" +
                   "      SELECT odr.vehicle_id FROM obd2_device_registrations odr " +
                   "      WHERE odr.status = 'ACTIVE' AND odr.deleted_at IS NULL" +
                   "  )", nativeQuery = true)
    List<VehiclePersistenceEntity> findAvailableForLinkingByBranchId(@Param("branchId") UUID branchId);

    Optional<VehiclePersistenceEntity> findByVin(String vin);

    Optional<VehiclePersistenceEntity> findByPlateNumber(String plateNumber);
}
