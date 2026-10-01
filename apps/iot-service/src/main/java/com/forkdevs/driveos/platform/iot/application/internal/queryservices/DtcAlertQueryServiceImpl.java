package com.forkdevs.driveos.platform.iot.application.internal.queryservices;

import com.forkdevs.driveos.platform.iot.application.queryservices.DtcAlertQueryService;
import com.forkdevs.driveos.platform.iot.domain.model.aggregates.DtcAlert;
import com.forkdevs.driveos.platform.iot.domain.model.queries.GetDtcAlertsByRegistrationIdQuery;
import com.forkdevs.driveos.platform.iot.domain.model.queries.GetVehicleDtcAlertHistoryQuery;
import com.forkdevs.driveos.platform.iot.domain.repositories.DtcAlertRepository;
import com.forkdevs.driveos.platform.iot.domain.repositories.Obd2DeviceRegistrationRepository;
import com.forkdevs.driveos.platform.iot.domain.repositories.VehicleRegistrationRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collections;
import java.util.List;

/**
 * Service implementation for handling DTC Alert queries inside the iot context.
 */
@Service
public class DtcAlertQueryServiceImpl implements DtcAlertQueryService {

    private final DtcAlertRepository dtcAlertRepository;
    private final VehicleRegistrationRepository vehicleRegistrationRepository;
    private final Obd2DeviceRegistrationRepository obd2DeviceRegistrationRepository;

    public DtcAlertQueryServiceImpl(
            DtcAlertRepository dtcAlertRepository,
            VehicleRegistrationRepository vehicleRegistrationRepository,
            Obd2DeviceRegistrationRepository obd2DeviceRegistrationRepository
    ) {
        this.dtcAlertRepository = dtcAlertRepository;
        this.vehicleRegistrationRepository = vehicleRegistrationRepository;
        this.obd2DeviceRegistrationRepository = obd2DeviceRegistrationRepository;
    }

    @Override
    @Transactional(readOnly = true)
    public List<DtcAlert> handle(GetDtcAlertsByRegistrationIdQuery query) {
        return dtcAlertRepository.findAllByRegistrationId(query.obd2DeviceRegistrationId());
    }

    @Override
    @Transactional(readOnly = true)
    public List<DtcAlert> handle(GetVehicleDtcAlertHistoryQuery query) {
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

        return dtcAlertRepository.findAllByRegistrationIdAndCreatedAtGreaterThanEqual(
                activeObd2Reg.getId(),
                startTimestamp
        );
    }
}
