package com.forkdevs.driveos.platform.core.domain.repositories;

import com.forkdevs.driveos.platform.core.domain.model.aggregates.Owner;
import com.forkdevs.driveos.platform.core.domain.model.valueobjects.OwnerId;
import com.forkdevs.driveos.platform.core.domain.model.valueobjects.UserId;

import java.util.Optional;

public interface OwnerRepository {
    Owner save(Owner owner);
    Optional<Owner> findById(OwnerId id);
    Optional<Owner> findByUserId(UserId userId);
    boolean existsById(OwnerId id);
    boolean existsByUserId(UserId userId);
    Optional<Owner> findByDocumentNumber(String documentNumber);
    void delete(Owner owner);
}
