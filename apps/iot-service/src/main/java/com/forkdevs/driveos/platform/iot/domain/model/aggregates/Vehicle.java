package com.forkdevs.driveos.platform.iot.domain.model.aggregates;

import com.forkdevs.driveos.platform.shared.domain.model.aggregates.AbstractDomainAggregateRoot;
import com.forkdevs.driveos.platform.shared.domain.model.valueobjects.VehicleId;
import lombok.Getter;

import java.time.Instant;

/**
 * Domain Aggregate Root representing a Vehicle within the iot bounded context.
 */
@Getter
public class Vehicle extends AbstractDomainAggregateRoot<Vehicle> {

    private VehicleId id;
    private String plateNumber;
    private String brand;
    private String model;
    private Integer year;
    private String vin;
    private Long version;
    private Instant createdAt;
    private Instant updatedAt;
    private Instant deletedAt;

    public Vehicle() {
    }

    public Vehicle(String plateNumber, String brand, String model, Integer year, String vin) {
        this.id = null;
        this.plateNumber = plateNumber;
        this.brand = brand;
        this.model = model;
        this.year = year;
        this.vin = vin;
    }

    public Vehicle(
            VehicleId id,
            String plateNumber,
            String brand,
            String model,
            Integer year,
            String vin,
            Long version,
            Instant createdAt,
            Instant updatedAt,
            Instant deletedAt
    ) {
        this.id = id;
        this.plateNumber = plateNumber;
        this.brand = brand;
        this.model = model;
        this.year = year;
        this.vin = vin;
        this.version = version;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
        this.deletedAt = deletedAt;
    }

    /**
     * Updates the vehicle details.
     * @param plateNumber the new plate number
     * @param brand the new brand
     * @param model the new model
     * @param year the new manufacturing year
     * @param vin the new Vehicle Identification Number (VIN)
     */
    public void updateDetails(String plateNumber, String brand, String model, Integer year, String vin) {
        this.plateNumber = plateNumber;
        this.brand = brand;
        this.model = model;
        this.year = year;
        this.vin = vin;
    }
}
