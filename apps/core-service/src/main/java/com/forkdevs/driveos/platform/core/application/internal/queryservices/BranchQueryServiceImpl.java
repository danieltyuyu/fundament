package com.forkdevs.driveos.platform.core.application.internal.queryservices;

import com.forkdevs.driveos.platform.core.application.queryservices.BranchQueryService;
import com.forkdevs.driveos.platform.core.domain.model.aggregates.Branch;
import com.forkdevs.driveos.platform.core.domain.model.queries.GetAllBranchesByWorkshopIdQuery;
import com.forkdevs.driveos.platform.core.domain.model.queries.GetBranchByIdQuery;
import com.forkdevs.driveos.platform.core.domain.model.queries.GetIssuerTaxIdByBranchIdQuery;
import com.forkdevs.driveos.platform.core.domain.repositories.BranchRepository;
import com.forkdevs.driveos.platform.core.domain.repositories.WorkshopRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class BranchQueryServiceImpl implements BranchQueryService {
    private final BranchRepository branchRepository;
    private final WorkshopRepository workshopRepository;

    public BranchQueryServiceImpl(BranchRepository branchRepository, WorkshopRepository workshopRepository) {
        this.branchRepository = branchRepository;
        this.workshopRepository = workshopRepository;
    }

    @Override
    public Optional<Branch> handle(GetBranchByIdQuery query) {
        return branchRepository.findById(query.id());
    }

    @Override
    public List<Branch> handle(GetAllBranchesByWorkshopIdQuery query) {
        return branchRepository.findAllByWorkshopId(query.workshopId());
    }

    @Override
    public Optional<String> handle(GetIssuerTaxIdByBranchIdQuery query) {
        return branchRepository.findById(query.branchId())
                .flatMap(branch -> workshopRepository.findById(branch.getWorkshopId()))
                .map(workshop -> workshop.getTaxId().value());
    }
}

