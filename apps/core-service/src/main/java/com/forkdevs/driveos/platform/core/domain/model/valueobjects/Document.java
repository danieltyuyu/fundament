package com.forkdevs.driveos.platform.core.domain.model.valueobjects;

import jakarta.persistence.Embeddable;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import lombok.Getter;

@Embeddable
public class Document {

    @Enumerated(EnumType.STRING)
    @Getter
    private DocumentType documentType;

    @Getter
    private String documentNumber;

    private static final String NOT_BLANK_DOCUMENT_NUMBER_MESSAGE_KEY = "core.error.documentNumber.notBlank";
    private static final String NOT_NULL_DOCUMENT_TYPE_MESSAGE_KEY = "core.error.documentType.notNull";

    public Document() {
    }

    public Document(DocumentType type, String number) {
        if (type == null) {
            throw new IllegalArgumentException(NOT_NULL_DOCUMENT_TYPE_MESSAGE_KEY);
        }
        if (number == null || number.isBlank()) {
            throw new IllegalArgumentException(NOT_BLANK_DOCUMENT_NUMBER_MESSAGE_KEY);
        }
        this.documentType = type;
        this.documentNumber = number;
    }

    public Document(String type, String number) {
        this(DocumentType.valueOf(type.toUpperCase()), number);
    }
}
