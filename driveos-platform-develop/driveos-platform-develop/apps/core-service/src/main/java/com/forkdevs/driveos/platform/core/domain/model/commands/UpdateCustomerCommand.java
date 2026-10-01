package com.forkdevs.driveos.platform.core.domain.model.commands;

import com.forkdevs.driveos.platform.core.domain.model.valueobjects.Document;
import com.forkdevs.driveos.platform.core.domain.model.valueobjects.PersonName;
import com.forkdevs.driveos.platform.core.domain.model.valueobjects.Phone;
import com.forkdevs.driveos.platform.shared.domain.model.valueobjects.CustomerId;

public record UpdateCustomerCommand(
        CustomerId customerId,
        PersonName name,
        String businessName,
        Document document,
        Phone phone
) {
}
