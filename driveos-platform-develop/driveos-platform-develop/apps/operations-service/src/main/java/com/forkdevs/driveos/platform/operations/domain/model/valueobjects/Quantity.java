package com.forkdevs.driveos.platform.operations.domain.model.valueobjects;

/**
 * Value object representing a quantity of items or services.
 * @param value the integer value representing the quantity. It must be a positive integer greater than zero.
 * @author Joel Huamani Estefanero
 */
public record Quantity(Integer value) {
    private static final String NOT_NULL_MESSAGE_KEY = "operations.error.quantity.required";
    private static final String STRICTLY_POSITIVE_MESSAGE_KEY = "operations.error.quantity.mustBeGreaterThanZero";

    /**
     * Constructor for Quantity that performs validation on the input value to ensure it is not null and is a strictly positive integer.
     * @param value the integer value representing the quantity to be validated and assigned to the record. It must not be null and must be greater than zero.
     * @throws IllegalArgumentException if the value is null or not a strictly positive integer, with appropriate error messages defined in the constants.
     */
    public Quantity {
        if (value == null) {
            throw new IllegalArgumentException(NOT_NULL_MESSAGE_KEY);
        }
        if (value <= 0) {
            throw new IllegalArgumentException(STRICTLY_POSITIVE_MESSAGE_KEY);
        }
    }
}
