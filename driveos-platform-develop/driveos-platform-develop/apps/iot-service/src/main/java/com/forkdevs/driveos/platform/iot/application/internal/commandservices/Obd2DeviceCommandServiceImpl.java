package com.forkdevs.driveos.platform.iot.application.internal.commandservices;

import com.forkdevs.driveos.platform.iot.application.commandservices.Obd2DeviceCommandFailure;
import com.forkdevs.driveos.platform.iot.application.commandservices.Obd2DeviceCommandService;
import com.forkdevs.driveos.platform.iot.domain.model.aggregates.Obd2Device;
import com.forkdevs.driveos.platform.iot.domain.model.commands.CreateObd2DeviceCommand;
import com.forkdevs.driveos.platform.iot.domain.model.commands.DeleteObd2DeviceCommand;
import com.forkdevs.driveos.platform.iot.domain.model.commands.UpdateObd2DeviceCommand;
import com.forkdevs.driveos.platform.iot.domain.model.valueobjects.Obd2DeviceStatus;
import com.forkdevs.driveos.platform.iot.domain.repositories.Obd2DeviceRepository;
import com.forkdevs.driveos.platform.shared.application.result.Result;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Service implementation for handling OBD2 Device command cases.
 */
@Service
public class Obd2DeviceCommandServiceImpl implements Obd2DeviceCommandService {

    private final Obd2DeviceRepository obd2DeviceRepository;

    public Obd2DeviceCommandServiceImpl(Obd2DeviceRepository obd2DeviceRepository) {
        this.obd2DeviceRepository = obd2DeviceRepository;
    }

    @Override
    @Transactional
    public Result<Obd2Device, Obd2DeviceCommandFailure> handle(CreateObd2DeviceCommand command) {
        if (obd2DeviceRepository.existsByMacAddress(command.macAddress())) {
            return Result.failure(new Obd2DeviceCommandFailure.Duplicate("iot.error.obd2Device.macAlreadyExists"));
        }

        var obd2Device = new Obd2Device(command.branchId(), command.macAddress());
        var savedDevice = obd2DeviceRepository.save(obd2Device);

        return Result.success(savedDevice);
    }

    @Override
    @Transactional
    public Result<Void, Obd2DeviceCommandFailure> handle(DeleteObd2DeviceCommand command) {
        var obd2DeviceOpt = obd2DeviceRepository.findById(command.obd2DeviceId());
        if (obd2DeviceOpt.isEmpty()) {
            return Result.failure(new Obd2DeviceCommandFailure.NotFound("iot.error.obd2Device.notFound"));
        }

        var obd2Device = obd2DeviceOpt.get();
        if (Obd2DeviceStatus.LINKED.equals(obd2Device.getStatus())) {
            return Result.failure(new Obd2DeviceCommandFailure.InvalidState("iot.error.obd2Device.cannotDeleteLinkedDevice"));
        }

        obd2DeviceRepository.delete(command.obd2DeviceId());
        return Result.success(null);
    }

    @Override
    @Transactional
    public Result<Obd2Device, Obd2DeviceCommandFailure> handle(UpdateObd2DeviceCommand command) {
        var obd2DeviceOpt = obd2DeviceRepository.findById(command.id());
        if (obd2DeviceOpt.isEmpty()) {
            return Result.failure(new Obd2DeviceCommandFailure.NotFound("iot.error.obd2Device.notFound"));
        }

        var obd2Device = obd2DeviceOpt.get();

        // Si la dirección MAC cambia, validar duplicados
        if (!obd2Device.getMacAddress().equalsIgnoreCase(command.macAddress())) {
            var existingDeviceOpt = obd2DeviceRepository.findByMacAddress(command.macAddress());
            if (existingDeviceOpt.isPresent()) {
                return Result.failure(new Obd2DeviceCommandFailure.Duplicate("iot.error.obd2Device.macAlreadyExists"));
            }
        }

        obd2Device.updateMacAddress(command.macAddress());
        var savedDevice = obd2DeviceRepository.save(obd2Device);

        return Result.success(savedDevice);
    }
}
