package com.forkdevs.driveos.platform.iot.application.internal.commandservices;

import com.forkdevs.driveos.platform.iot.application.commandservices.TelemetryCommandFailure;
import com.forkdevs.driveos.platform.iot.domain.model.aggregates.Obd2Device;
import com.forkdevs.driveos.platform.iot.domain.model.aggregates.Obd2DeviceRegistration;
import com.forkdevs.driveos.platform.iot.domain.model.commands.IngestTelemetryBatchCommand;
import com.forkdevs.driveos.platform.iot.domain.model.valueobjects.Obd2DeviceId;
import com.forkdevs.driveos.platform.iot.domain.model.valueobjects.Obd2DeviceRegistrationId;
import com.forkdevs.driveos.platform.iot.domain.repositories.Obd2DeviceRegistrationRepository;
import com.forkdevs.driveos.platform.iot.domain.repositories.Obd2DeviceRepository;
import com.forkdevs.driveos.platform.iot.domain.repositories.TelemetrySnapshotRepository;
import com.forkdevs.driveos.platform.shared.domain.model.valueobjects.BranchId;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class TelemetryCommandServiceImplTests {

    @Mock
    private TelemetrySnapshotRepository telemetrySnapshotRepository;

    @Mock
    private Obd2DeviceRepository obd2DeviceRepository;

    @Mock
    private Obd2DeviceRegistrationRepository obd2DeviceRegistrationRepository;

    @InjectMocks
    private TelemetryCommandServiceImpl telemetryCommandService;

    @Test
    void handle_DeviceNotFound_ReturnsNotFoundFailure() {
        Obd2DeviceId deviceId = Obd2DeviceId.random();
        IngestTelemetryBatchCommand command = new IngestTelemetryBatchCommand(deviceId, List.of());

        when(obd2DeviceRepository.findById(deviceId)).thenReturn(Optional.empty());

        var result = telemetryCommandService.handle(command);

        assertTrue(result.isFailure());
        assertTrue(result.failure().isPresent());
        assertInstanceOf(TelemetryCommandFailure.NotFound.class, result.failure().get());
        verify(obd2DeviceRegistrationRepository, never()).findActiveByObd2DeviceId(any());
    }

    @Test
    void handle_NoActiveRegistration_ReturnsInvalidStateFailure() {
        Obd2DeviceId deviceId = Obd2DeviceId.random();
        Obd2Device device = mock(Obd2Device.class);
        IngestTelemetryBatchCommand command = new IngestTelemetryBatchCommand(deviceId, List.of());

        when(obd2DeviceRepository.findById(deviceId)).thenReturn(Optional.of(device));
        when(obd2DeviceRegistrationRepository.findActiveByObd2DeviceId(deviceId)).thenReturn(Optional.empty());

        var result = telemetryCommandService.handle(command);

        assertTrue(result.isFailure());
        assertTrue(result.failure().isPresent());
        assertInstanceOf(TelemetryCommandFailure.InvalidState.class, result.failure().get());
    }

    @Test
    void handle_ValidDeviceAndRegistration_IngestsSnapshotsSuccessfully() {
        Obd2DeviceId deviceId = Obd2DeviceId.random();
        Obd2Device device = mock(Obd2Device.class);
        Obd2DeviceRegistration registration = mock(Obd2DeviceRegistration.class);

        when(registration.getId()).thenReturn(Obd2DeviceRegistrationId.random());
        when(registration.getBranchId()).thenReturn(new BranchId(UUID.randomUUID()));

        IngestTelemetryBatchCommand.TelemetrySnapshotData snapshotData = new IngestTelemetryBatchCommand.TelemetrySnapshotData(
                3000, 85, 75.0, 12000, 80.0, Instant.now()
        );
        IngestTelemetryBatchCommand command = new IngestTelemetryBatchCommand(deviceId, List.of(snapshotData));

        when(obd2DeviceRepository.findById(deviceId)).thenReturn(Optional.of(device));
        when(obd2DeviceRegistrationRepository.findActiveByObd2DeviceId(deviceId)).thenReturn(Optional.of(registration));
        when(telemetrySnapshotRepository.saveAll(anyList())).thenAnswer(invocation -> invocation.getArgument(0));

        var result = telemetryCommandService.handle(command);

        assertTrue(result.isSuccess());
        verify(device).ping();
        verify(obd2DeviceRepository).save(device);
        verify(telemetrySnapshotRepository).saveAll(anyList());
    }
}
