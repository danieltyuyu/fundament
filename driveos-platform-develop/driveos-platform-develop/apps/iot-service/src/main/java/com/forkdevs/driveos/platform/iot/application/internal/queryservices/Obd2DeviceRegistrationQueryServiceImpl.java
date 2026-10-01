package com.forkdevs.driveos.platform.iot.application.internal.queryservices;

import com.forkdevs.driveos.platform.iot.application.queryservices.Obd2DeviceRegistrationQueryService;
import com.forkdevs.driveos.platform.iot.domain.model.aggregates.Obd2DeviceRegistration;
import com.forkdevs.driveos.platform.iot.domain.model.queries.GetObd2DeviceRegistrationsByBranchIdAndStatusQuery;
import com.forkdevs.driveos.platform.iot.domain.repositories.Obd2DeviceRegistrationRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * Service implementation for handling OBD2 device registration queries.
 */
@Service
public class Obd2DeviceRegistrationQueryServiceImpl implements Obd2DeviceRegistrationQueryService {

    private final Obd2DeviceRegistrationRepository obd2DeviceRegistrationRepository;

    public Obd2DeviceRegistrationQueryServiceImpl(Obd2DeviceRegistrationRepository obd2DeviceRegistrationRepository) {
        this.obd2DeviceRegistrationRepository = obd2DeviceRegistrationRepository;
    }

    @Override
    @Transactional(readOnly = true)
    public List<Obd2DeviceRegistration> handle(GetObd2DeviceRegistrationsByBranchIdAndStatusQuery query) {
        return obd2DeviceRegistrationRepository.findAllByBranchIdAndStatus(query.branchId(), query.status());
    }
}
