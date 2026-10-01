package com.forkdevs.driveos.platform.operations.domain.model.queries;

import com.forkdevs.driveos.platform.operations.domain.model.valueobjects.ServiceId;

/**
 * Query to get a service by its ID. This query encapsulates the necessary identifier to perform the retrieval operation.
 * @param id the unique identifier of the service to be retrieved
 * @author Joel Huamani Estefanero
 */
public record GetServiceByIdQuery(ServiceId id) {
}
