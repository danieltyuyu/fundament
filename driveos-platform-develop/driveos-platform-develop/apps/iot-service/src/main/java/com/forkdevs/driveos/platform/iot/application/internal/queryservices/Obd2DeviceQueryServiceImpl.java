package com.forkdevs.driveos.platform.iot.application.internal.queryservices;

import com.forkdevs.driveos.platform.iot.application.queryservices.Obd2DeviceQueryService;
import com.forkdevs.driveos.platform.iot.domain.model.aggregates.Obd2Device;
import com.forkdevs.driveos.platform.iot.domain.model.queries.GetAvailableObd2DevicesQuery;
import com.forkdevs.driveos.platform.iot.domain.model.queries.GetObd2DeviceByIdQuery;
import com.forkdevs.driveos.platform.iot.domain.model.queries.GetObd2DevicesByBranchIdQuery;
import com.forkdevs.driveos.platform.iot.domain.model.valueobjects.Obd2DeviceStatus;
import com.forkdevs.driveos.platform.iot.domain.repositories.Obd2DeviceRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

/**
 * Service implementation for handling OBD2 device queries.
 */
@Service
public class Obd2DeviceQueryServiceImpl implements Obd2DeviceQueryService {

    private final Obd2DeviceRepository obd2DeviceRepository;

    public Obd2DeviceQueryServiceImpl(Obd2DeviceRepository obd2DeviceRepository) {
        this.obd2DeviceRepository = obd2DeviceRepository;
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<Obd2Device> handle(GetObd2DeviceByIdQuery query) {
        return obd2DeviceRepository.findById(query.obd2DeviceId());
    }

    @Override
    @Transactional(readOnly = true)
    public List<Obd2Device> handle(GetObd2DevicesByBranchIdQuery query) {
        return obd2DeviceRepository.findAllByBranchId(query.branchId());
    }

    @Override
    @Transactional(readOnly = true)
    public List<Obd2Device> handle(GetAvailableObd2DevicesQuery query) {
        return obd2DeviceRepository.findAllByBranchIdAndStatus(query.branchId(), Obd2DeviceStatus.AVAILABLE);
    }
}
