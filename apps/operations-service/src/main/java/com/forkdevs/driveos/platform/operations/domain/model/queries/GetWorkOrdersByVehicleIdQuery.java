package com.forkdevs.driveos.platform.operations.domain.model.queries;

import com.forkdevs.driveos.platform.shared.domain.model.valueobjects.VehicleId;

/**
 * Query to retrieve all Work Orders associated with a specific Vehicle ID. This query encapsulates the necessary identifier to perform the retrieval operation.
 * @param vehicleId
 * @author Joel Huamani Estefanero
 */
public record GetWorkOrdersByVehicleIdQuery(VehicleId vehicleId) {}
