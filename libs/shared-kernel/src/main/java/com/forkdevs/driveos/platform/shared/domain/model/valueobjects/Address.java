package com.forkdevs.driveos.platform.shared.domain.model.valueobjects;

/**
 * Value object representing an address.
 * @param value
 * @author Joel Huamani Estefanero
 */
public record Address(String value) {
    private static final int MAX_LENGTH = 100;
    private static final String NOT_BLANK_MESSAGE_KEY = "operations.error.address.notBlank";
    private static final String SIZE_MESSAGE_KEY = "operations.error.address.tooLong";

    /**
     * Constructs an Address value object with validation.
     * @param value the address string
     * @throws IllegalArgumentException if the address is null, blank, or exceeds maximum length
     */
    public Address {
        if (value == null || value.isBlank()) {
            throw new IllegalArgumentException(NOT_BLANK_MESSAGE_KEY);
        }
        if (value.length() > MAX_LENGTH) {
            throw new IllegalArgumentException(SIZE_MESSAGE_KEY);
        }
    }
}
