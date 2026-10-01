package com.forkdevs.driveos.platform.operations.domain.model.valueobjects;

/**
 * Value object representing a task description in the operations domain.
 * @param value the task description string, which must be between 10 and 1000 characters in length and cannot be blank.
 * @author Joel Huamani Estefanero
 */
public record TaskDescription(String value) {

    private static final int MIN_LENGTH = 10;
    private static final int MAX_LENGTH = 1000;
    private static final String NOT_BLANK_KEY = "operations.error.taskDescription.required";
    private static final String TOO_SHORT_KEY = "operations.error.taskDescription.tooShort";
    private static final String TOO_LONG_KEY = "operations.error.taskDescription.tooLong";

    /**
     * Constructor that validates the task description value.
     * @param value the task description string to be encapsulated by this TaskDescription
     * @throws IllegalArgumentException if the value is null, blank, shorter than MIN_LENGTH, or longer than MAX_LENGTH, with specific error messages for each validation failure
     */
    public TaskDescription {
        if (value == null || value.isBlank()) {
            throw new IllegalArgumentException(NOT_BLANK_KEY);
        }
        if (value.trim().length() < MIN_LENGTH) {
            throw new IllegalArgumentException(TOO_SHORT_KEY);
        }
        if (value.length() > MAX_LENGTH) {
            throw new IllegalArgumentException(TOO_LONG_KEY);
        }
    }
}
