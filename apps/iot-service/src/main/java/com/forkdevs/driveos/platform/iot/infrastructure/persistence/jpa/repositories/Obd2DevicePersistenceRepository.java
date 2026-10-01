package com.forkdevs.driveos.platform.iot.infrastructure.persistence.jpa.repositories;

import com.forkdevs.driveos.platform.iot.infrastructure.persistence.jpa.entities.Obd2DevicePersistenceEntity;
import com.forkdevs.driveos.platform.shared.domain.model.valueobjects.BranchId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * Spring Data JPA Repository for "obd2_devices".
 */
@Repository
public interface Obd2DevicePersistenceRepository extends JpaRepository<Obd2DevicePersistenceEntity, UUID> {

    Optional<Obd2DevicePersistenceEntity> findByMacAddress(String macAddress);

    boolean existsByMacAddress(String macAddress);

    List<Obd2DevicePersistenceEntity> findAllByBranchId(BranchId branchId);

    List<Obd2DevicePersistenceEntity> findAllByBranchIdAndStatus(BranchId branchId, String status);
}
