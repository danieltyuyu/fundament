package com.forkdevs.driveos.platform.core.domain.model.valueobjects;

import jakarta.persistence.Embeddable;

@Embeddable
public record PersonName(String firstName, String lastName) {

    private static final String NOT_BLANK_FIRST_NAME_MESSAGE_KEY = "core.error.firstName.notBlank";
    private static final String NOT_BLANK_LAST_NAME_MESSAGE_KEY = "core.error.lastName.notBlank";

    public PersonName {
        if (firstName == null || firstName.isBlank()) {
            throw new IllegalArgumentException(NOT_BLANK_FIRST_NAME_MESSAGE_KEY);
        }
        if (lastName == null || lastName.isBlank()) {
            throw new IllegalArgumentException(NOT_BLANK_LAST_NAME_MESSAGE_KEY);
        }
    }

    public String getFullName() {
        return String.format("%s %s", firstName, lastName);
    }
}
