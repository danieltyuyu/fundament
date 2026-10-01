package com.forkdevs.driveos.platform.shared.domain.model.valueobjects;

import java.math.BigDecimal;
import java.math.RoundingMode;

/**
 * Value Object representing a monetary amount. It encapsulates a BigDecimal to ensure precision and provides
 * basic operations for addition, subtraction, and multiplication. The amount must be non-null and non-negative.
 * @param amount The monetary amount represented as a BigDecimal. Must be non-null and non-negative.
 * @author Joel Huamani Estefanero
 */
public record Money(BigDecimal amount) {

    public static final Money ZERO = new Money(BigDecimal.ZERO);

    private static final String NOT_NULL_MESSAGE_KEY = "operations.error.money.required";
    private static final String NOT_NEGATIVE_MESSAGE_KEY = "operations.error.money.cannotBeNegative";

    /**
     * Constructor for Money. Validates that the amount is non-null and non-negative, and sets the scale to 2 decimal places.
     * @param amount
     * @throws IllegalArgumentException if the amount is null or negative
     */
    public Money {
        if (amount == null) {
            throw new IllegalArgumentException(NOT_NULL_MESSAGE_KEY);
        }
        if (amount.compareTo(BigDecimal.ZERO) < 0) {
            throw new IllegalArgumentException(NOT_NEGATIVE_MESSAGE_KEY);
        }

        amount = amount.setScale(2, RoundingMode.HALF_UP);
    }

    /**
     * Adds another Money instance to this one and returns a new Money instance with the result. If the other instance is null, it returns this instance.
     * @param other The other Money instance to add. Can be null, in which case this instance is returned.
     * @return A new Money instance representing the sum of this and the other. If the other is null, returns this instance.
     */
    public Money plus(Money other) {
        if (other == null) return this;
        return new Money(this.amount.add(other.amount));
    }

    /**
     * Subtracts another Money instance from this one and returns a new Money instance with the result. If the other instance is null, it returns this instance.
     * @param other The other Money instance to subtract. Can be null, in which case this instance is returned.
     * @return A new Money instance representing the difference between this and the other. If the other is null, returns this instance.
     */
    public Money minus(Money other) {
        if (other == null) return this;
        return new Money(this.amount.subtract(other.amount));
    }

    /**
     * Multiplies this Money instance by a given quantity and returns a new Money instance with the result. The quantity is converted to a BigDecimal for multiplication.
     * @param quantity The quantity to multiply by. Must be a non-negative integer.
     * @return A new Money instance representing the product of this amount and the given quantity.
     */
    public Money multiply(int quantity) {
        return new Money(this.amount.multiply(BigDecimal.valueOf(quantity)));
    }

    /**
     * Multiplies this Money instance by a given factor and returns a new Money instance with the result. The factor is a BigDecimal that can represent fractional values, allowing for more precise calculations.
     * @param factor The factor to multiply by. Must be a non-negative BigDecimal. If null, this instance is returned.
     * @return A new Money instance representing the product of this amount and the given factor. If the factor is null, returns this instance.
     */
    public Money multiply(BigDecimal factor) {
        if (factor == null) return this;
        return new Money(this.amount.multiply(factor));
    }

    /**
     * Compares this Money instance to another and returns true if this amount is greater than the other amount. If the other instance is null, it returns false.
     * @param other The other Money instance to compare to. Can be null, in which case this method returns false.
     * @return true if this amount is greater than the other amount, false otherwise. If the other instance is null, returns false.
     */
    public boolean isGreaterThan(Money other) {
        return other != null && this.amount.compareTo(other.amount) > 0;
    }

    /**
     * Compares this Money instance to another and returns true if this amount is less than the other amount. If the other instance is null, it returns false.
     * @param other The other Money instance to compare to. Can be null, in which case this method returns false.
     * @return true if this amount is less than the other amount, false otherwise. If the other instance is null, returns false.
     */
    public boolean isLessThan(Money other) {
        return other != null && this.amount.compareTo(other.amount) < 0;
    }
}
