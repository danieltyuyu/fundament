package com.forkdevs.driveos.platform.shared.domain.model.valueobjects;

/**
 * Value Object representing the mileage of a vehicle.
 * It encapsulates the mileage value and enforces validation rules to ensure data integrity.
 * @param value The mileage value, which must be a non-negative integer and cannot be null.
 * @author Joel Huamani Estefanero
 */
public record Mileage(Integer value) {
    private static final String NOT_NULL_MESSAGE_KEY = "operations.error.mileage.required";
    private static final String NOT_NEGATIVE_MESSAGE_KEY = "operations.error.mileage.cannotBeNegative";

    /**
     * Constructor for Mileage that performs validation on the input value.
     * @param value The mileage value to be validated and assigned to the record. It must not be null and must be a non-negative integer.
     * @throws IllegalArgumentException if the value is null or negative, with appropriate error messages defined in the constants.
     */
    public Mileage {
        if (value == null) {
            throw new IllegalArgumentException(NOT_NULL_MESSAGE_KEY);
        }
        if (value < 0) {
            throw new IllegalArgumentException(NOT_NEGATIVE_MESSAGE_KEY);
        }
    }
}
