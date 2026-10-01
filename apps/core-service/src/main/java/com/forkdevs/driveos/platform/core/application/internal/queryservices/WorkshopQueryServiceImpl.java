package com.forkdevs.driveos.platform.core.application.internal.queryservices;

import com.forkdevs.driveos.platform.core.application.queryservices.WorkshopQueryService;
import com.forkdevs.driveos.platform.core.domain.model.aggregates.Workshop;
import com.forkdevs.driveos.platform.core.domain.model.queries.GetAllWorkshopsByOwnerIdQuery;
import com.forkdevs.driveos.platform.core.domain.model.queries.GetWorkshopByIdQuery;
import com.forkdevs.driveos.platform.core.domain.repositories.WorkshopRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class WorkshopQueryServiceImpl implements WorkshopQueryService {
    private final WorkshopRepository workshopRepository;

    public WorkshopQueryServiceImpl(WorkshopRepository workshopRepository) {
        this.workshopRepository = workshopRepository;
    }

    @Override
    public Optional<Workshop> handle(GetWorkshopByIdQuery query) {
        return workshopRepository.findById(query.id());
    }

    @Override
    public List<Workshop> handle(GetAllWorkshopsByOwnerIdQuery query) {
        return workshopRepository.findAllByOwnerId(query.ownerId());
    }
}
