package com.forkdevs.driveos.platform.iot.application.internal.commandservices;

import com.forkdevs.driveos.platform.iot.application.commandservices.Obd2DeviceRegistrationCommandFailure;
import com.forkdevs.driveos.platform.iot.application.commandservices.Obd2DeviceRegistrationCommandService;
import com.forkdevs.driveos.platform.iot.domain.model.aggregates.Obd2DeviceRegistration;
import com.forkdevs.driveos.platform.iot.domain.model.commands.DeactivateObd2DeviceRegistrationCommand;
import com.forkdevs.driveos.platform.iot.domain.model.commands.LinkObd2DeviceToVehicleCommand;
import com.forkdevs.driveos.platform.iot.domain.model.valueobjects.Obd2DeviceStatus;
import com.forkdevs.driveos.platform.iot.domain.repositories.Obd2DeviceRegistrationRepository;
import com.forkdevs.driveos.platform.iot.domain.repositories.Obd2DeviceRepository;
import com.forkdevs.driveos.platform.shared.application.result.Result;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Service implementation for handling OBD2 Device registration/linking commands.
 */
@Service
public class Obd2DeviceRegistrationCommandServiceImpl implements Obd2DeviceRegistrationCommandService {

    private final Obd2DeviceRepository obd2DeviceRepository;
    private final Obd2DeviceRegistrationRepository obd2DeviceRegistrationRepository;

    public Obd2DeviceRegistrationCommandServiceImpl(
            Obd2DeviceRepository obd2DeviceRepository,
            Obd2DeviceRegistrationRepository obd2DeviceRegistrationRepository
    ) {
        this.obd2DeviceRepository = obd2DeviceRepository;
        this.obd2DeviceRegistrationRepository = obd2DeviceRegistrationRepository;
    }

    @Override
    @Transactional
    public Result<Obd2DeviceRegistration, Obd2DeviceRegistrationCommandFailure> handle(LinkObd2DeviceToVehicleCommand command) {
        // 1. Validar existencia del OBD2
        var obd2DeviceOpt = obd2DeviceRepository.findById(command.obd2DeviceId());
        if (obd2DeviceOpt.isEmpty()) {
            return Result.failure(new Obd2DeviceRegistrationCommandFailure.NotFound("iot.error.obd2Device.notFound"));
        }

        var obd2Device = obd2DeviceOpt.get();

        // 2. Validar que el OBD2 no esté ya marcado como LINKED en su agregado
        if (Obd2DeviceStatus.LINKED.equals(obd2Device.getStatus())) {
            return Result.failure(new Obd2DeviceRegistrationCommandFailure.InvalidState("iot.error.obd2DeviceRegistration.deviceAlreadyLinked"));
        }

        // 3. Validar que el OBD2 no tenga ya una vinculación activa en base de datos (invariante de negocio)
        var activeDeviceRegOpt = obd2DeviceRegistrationRepository.findActiveByObd2DeviceId(command.obd2DeviceId());
        if (activeDeviceRegOpt.isPresent()) {
            return Result.failure(new Obd2DeviceRegistrationCommandFailure.InvalidState("iot.error.obd2DeviceRegistration.deviceAlreadyLinked"));
        }

        // 4. Validar que el vehículo no tenga una vinculación activa en base de datos (invariante de negocio)
        var activeVehicleRegOpt = obd2DeviceRegistrationRepository.findActiveByVehicleId(command.vehicleId());
        if (activeVehicleRegOpt.isPresent()) {
            return Result.failure(new Obd2DeviceRegistrationCommandFailure.InvalidState("iot.error.obd2DeviceRegistration.vehicleAlreadyLinked"));
        }

        // 5. Actualizar el estado del OBD2 a LINKED y guardar
        obd2Device.markAsLinked();
        obd2DeviceRepository.save(obd2Device);

        // 6. Crear la vinculación con estado ACTIVE (por defecto en el constructor de Obd2DeviceRegistration)
        var registration = new Obd2DeviceRegistration(
                command.obd2DeviceId(),
                command.branchId(),
                command.vehicleId()
        );

        // 7. Guardar en base de datos
        var savedRegistration = obd2DeviceRegistrationRepository.save(registration);

        return Result.success(savedRegistration);
    }

    @Override
    @Transactional
    public Result<Obd2DeviceRegistration, Obd2DeviceRegistrationCommandFailure> handle(DeactivateObd2DeviceRegistrationCommand command) {
        // 1. Validar existencia de la vinculación
        var registrationOpt = obd2DeviceRegistrationRepository.findById(command.registrationId());
        if (registrationOpt.isEmpty()) {
            return Result.failure(new Obd2DeviceRegistrationCommandFailure.NotFound("iot.error.obd2DeviceRegistration.notFound"));
        }

        var registration = registrationOpt.get();

        // 2. Validar existencia del OBD2 asociado
        var obd2DeviceOpt = obd2DeviceRepository.findById(registration.getObd2DeviceId());
        if (obd2DeviceOpt.isEmpty()) {
            return Result.failure(new Obd2DeviceRegistrationCommandFailure.NotFound("iot.error.obd2Device.notFound"));
        }

        var obd2Device = obd2DeviceOpt.get();

        // 3. Desactivar vinculación
        try {
            registration.deactivate();
        } catch (IllegalStateException e) {
            return Result.failure(new Obd2DeviceRegistrationCommandFailure.InvalidState(e.getMessage()));
        }

        // 4. Actualizar el estado del OBD2 a AVAILABLE
        obd2Device.markAsAvailable();

        // 5. Guardar cambios en base de datos
        obd2DeviceRepository.save(obd2Device);
        var savedRegistration = obd2DeviceRegistrationRepository.save(registration);

        return Result.success(savedRegistration);
    }
}
