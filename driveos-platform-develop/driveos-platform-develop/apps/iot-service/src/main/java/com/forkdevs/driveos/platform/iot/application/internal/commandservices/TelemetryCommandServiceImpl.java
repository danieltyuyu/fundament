package com.forkdevs.driveos.platform.iot.application.internal.commandservices;

import com.forkdevs.driveos.platform.iot.application.commandservices.TelemetryCommandFailure;
import com.forkdevs.driveos.platform.iot.application.commandservices.TelemetryCommandService;
import com.forkdevs.driveos.platform.iot.domain.model.aggregates.Obd2Device;
import com.forkdevs.driveos.platform.iot.domain.model.aggregates.Obd2DeviceRegistration;
import com.forkdevs.driveos.platform.iot.domain.model.aggregates.TelemetrySnapshot;
import com.forkdevs.driveos.platform.iot.domain.model.commands.IngestTelemetryBatchCommand;
import com.forkdevs.driveos.platform.iot.domain.repositories.Obd2DeviceRegistrationRepository;
import com.forkdevs.driveos.platform.iot.domain.repositories.Obd2DeviceRepository;
import com.forkdevs.driveos.platform.iot.domain.repositories.TelemetrySnapshotRepository;
import com.forkdevs.driveos.platform.shared.application.result.Result;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Service implementation for handling telemetry ingestion command cases.
 */
@Service
public class TelemetryCommandServiceImpl implements TelemetryCommandService {

    private final TelemetrySnapshotRepository telemetrySnapshotRepository;
    private final Obd2DeviceRepository obd2DeviceRepository;
    private final Obd2DeviceRegistrationRepository obd2DeviceRegistrationRepository;

    public TelemetryCommandServiceImpl(
            TelemetrySnapshotRepository telemetrySnapshotRepository,
            Obd2DeviceRepository obd2DeviceRepository,
            Obd2DeviceRegistrationRepository obd2DeviceRegistrationRepository
    ) {
        this.telemetrySnapshotRepository = telemetrySnapshotRepository;
        this.obd2DeviceRepository = obd2DeviceRepository;
        this.obd2DeviceRegistrationRepository = obd2DeviceRegistrationRepository;
    }

    @Override
    @Transactional
    public Result<List<TelemetrySnapshot>, TelemetryCommandFailure> handle(IngestTelemetryBatchCommand command) {
        try {
            // 1. Verify that the device exists
            Obd2Device device = obd2DeviceRepository.findById(command.obd2DeviceId())
                    .orElseThrow(() -> new IllegalArgumentException("iot.error.obd2Device.notFound"));

            // 2. Find the active registration for this device
            Obd2DeviceRegistration registration = obd2DeviceRegistrationRepository
                    .findActiveByObd2DeviceId(command.obd2DeviceId())
                    .orElseThrow(() -> new IllegalStateException("iot.error.obd2DeviceRegistration.noActiveRegistration"));

            // 3. Update device last ping
            device.ping();
            obd2DeviceRepository.save(device);

            // 4. Map and save telemetry snapshots
            List<TelemetrySnapshot> snapshotsToSave = command.snapshots().stream()
                    .map(data -> new TelemetrySnapshot(
                            registration.getId(),
                            registration.getBranchId(),
                            data.rpm(),
                            data.temperature(),
                            data.speedKmh(),
                            data.odometerKm(),
                            data.fuelLevelPercent(),
                            data.createdAt()
                    ))
                    .collect(Collectors.toList());

            List<TelemetrySnapshot> savedSnapshots = telemetrySnapshotRepository.saveAll(snapshotsToSave);

            return Result.success(savedSnapshots);

        } catch (IllegalArgumentException e) {
            return Result.failure(new TelemetryCommandFailure.NotFound(e.getMessage()));
        } catch (IllegalStateException e) {
            return Result.failure(new TelemetryCommandFailure.InvalidState(e.getMessage()));
        }
    }
}
