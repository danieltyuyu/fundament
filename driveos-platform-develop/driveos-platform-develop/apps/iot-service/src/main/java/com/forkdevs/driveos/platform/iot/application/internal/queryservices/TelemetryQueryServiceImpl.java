package com.forkdevs.driveos.platform.iot.application.internal.queryservices;

import com.forkdevs.driveos.platform.iot.application.queryservices.TelemetryQueryService;
import com.forkdevs.driveos.platform.iot.domain.model.aggregates.TelemetrySnapshot;
import com.forkdevs.driveos.platform.iot.domain.model.queries.GetLatestTelemetrySnapshotQuery;
import com.forkdevs.driveos.platform.iot.domain.model.queries.GetTelemetrySnapshotHistoryQuery;
import com.forkdevs.driveos.platform.iot.domain.model.queries.GetTelemetrySnapshotsByRegistrationIdQuery;
import com.forkdevs.driveos.platform.iot.domain.model.queries.GetVehicleTelemetrySnapshotHistoryQuery;
import com.forkdevs.driveos.platform.iot.domain.repositories.Obd2DeviceRegistrationRepository;
import com.forkdevs.driveos.platform.iot.domain.repositories.TelemetrySnapshotRepository;
import com.forkdevs.driveos.platform.iot.domain.repositories.VehicleRegistrationRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

/**
 * Service implementation for handling telemetry queries.
 */
@Service
public class TelemetryQueryServiceImpl implements TelemetryQueryService {

    private final TelemetrySnapshotRepository telemetrySnapshotRepository;
    private final Obd2DeviceRegistrationRepository obd2DeviceRegistrationRepository;
    private final VehicleRegistrationRepository vehicleRegistrationRepository;

    public TelemetryQueryServiceImpl(
            TelemetrySnapshotRepository telemetrySnapshotRepository,
            Obd2DeviceRegistrationRepository obd2DeviceRegistrationRepository,
            VehicleRegistrationRepository vehicleRegistrationRepository
    ) {
        this.telemetrySnapshotRepository = telemetrySnapshotRepository;
        this.obd2DeviceRegistrationRepository = obd2DeviceRegistrationRepository;
        this.vehicleRegistrationRepository = vehicleRegistrationRepository;
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<TelemetrySnapshot> handle(GetLatestTelemetrySnapshotQuery query) {
        return obd2DeviceRegistrationRepository
                .findActiveByObd2DeviceId(query.obd2DeviceId())
                .flatMap(registration -> telemetrySnapshotRepository.findLatestByRegistrationId(registration.getId()));
    }

    @Override
    @Transactional(readOnly = true)
    public List<TelemetrySnapshot> handle(GetTelemetrySnapshotHistoryQuery query) {
        return obd2DeviceRegistrationRepository
                .findActiveByObd2DeviceId(query.obd2DeviceId())
                .map(registration -> telemetrySnapshotRepository.findAllByRegistrationId(registration.getId()))
                .orElse(Collections.emptyList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<TelemetrySnapshot> handle(GetTelemetrySnapshotsByRegistrationIdQuery query) {
        return telemetrySnapshotRepository.findAllByRegistrationId(query.obd2DeviceRegistrationId());
    }

    @Override
    @Transactional(readOnly = true)
    public List<TelemetrySnapshot> handle(GetVehicleTelemetrySnapshotHistoryQuery query) {
        var activeVehicleRegOpt = vehicleRegistrationRepository.findActiveByVehicleId(query.vehicleId());
        if (activeVehicleRegOpt.isEmpty()) {
            return Collections.emptyList();
        }
        var activeVehicleReg = activeVehicleRegOpt.get();
        var startTimestamp = activeVehicleReg.getCreatedAt();

        var activeObd2RegOpt = obd2DeviceRegistrationRepository.findActiveByVehicleId(query.vehicleId());
        if (activeObd2RegOpt.isEmpty()) {
            return Collections.emptyList();
        }
        var activeObd2Reg = activeObd2RegOpt.get();

        return telemetrySnapshotRepository.findAllByRegistrationIdAndCreatedAtGreaterThanEqual(
                activeObd2Reg.getId(),
                startTimestamp
        );
    }
}
