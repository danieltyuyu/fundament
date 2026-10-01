package com.forkdevs.driveos.platform.operations.domain.model.valueobjects;

/**
 * Value Object representing a Diagnostic Summary for a Work Order.
 * @param value the diagnostic summary string
 * @author Joel Huamani Estefanero
 */
public record DiagnosticSummary(String value) {
    private static final int MAX_LENGTH = 2000;
    private static final String NOT_BLANK_MESSAGE_KEY = "operations.error.diagnosticSummary.notBlank";
    private static final String SIZE_MESSAGE_KEY = "operations.error.diagnosticSummary.tooLong";

    /**
     * Constructor for DiagnosticSummary that performs validation on the input value.
     * @param value the diagnostic summary string to be validated and assigned to the record. It must not be null, blank, and must not exceed the maximum length defined by MAX_LENGTH.
     * @throws IllegalArgumentException if the value is null, blank, or exceeds the maximum length, with appropriate error messages defined in the constants.
     */
    public DiagnosticSummary {
        if (value == null || value.isBlank()) {
            throw new IllegalArgumentException(NOT_BLANK_MESSAGE_KEY);
        }
        if (value.length() > MAX_LENGTH) {
            throw new IllegalArgumentException(SIZE_MESSAGE_KEY);
        }
    }
}
