package com.forkdevs.driveos.platform.core.domain.model.commands;

import com.forkdevs.driveos.platform.core.domain.model.valueobjects.Phone;
import com.forkdevs.driveos.platform.core.domain.model.valueobjects.WorkshopId;
import com.forkdevs.driveos.platform.shared.domain.model.valueobjects.Address;

public record CreateBranchCommand(
        WorkshopId workshopId,
        String code,
        String name,
        Address address,
        Phone phone
) {
    public CreateBranchCommand {
        if (code == null || code.isBlank()) throw new IllegalArgumentException("core.error.code.required");
        if (name == null || name.isBlank()) throw new IllegalArgumentException("core.error.name.required");
    }
}
