package com.forkdevs.driveos.platform.core.application.internal.queryservices;

import com.forkdevs.driveos.platform.core.application.queryservices.OwnerQueryService;
import com.forkdevs.driveos.platform.core.domain.model.aggregates.Owner;
import com.forkdevs.driveos.platform.core.domain.model.queries.GetOwnerByIdQuery;
import com.forkdevs.driveos.platform.core.domain.model.queries.GetOwnerByUserIdQuery;
import com.forkdevs.driveos.platform.core.domain.repositories.OwnerRepository;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class OwnerQueryServiceImpl implements OwnerQueryService {
    private final OwnerRepository ownerRepository;

    public OwnerQueryServiceImpl(OwnerRepository ownerRepository) {
        this.ownerRepository = ownerRepository;
    }

    @Override
    public Optional<Owner> handle(GetOwnerByIdQuery query) {
        return ownerRepository.findById(query.id());
    }

    @Override
    public Optional<Owner> handle(GetOwnerByUserIdQuery query) {
        return ownerRepository.findByUserId(query.userId());
    }
}
