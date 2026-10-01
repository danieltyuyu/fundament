package com.forkdevs.driveos.platform.core.domain.model.valueobjects;

public record CreditCard(
        String cardNumber,
        String cardHolderName,
        String expirationDate,
        String cvv
) {
    public CreditCard {
        if (cardNumber == null || !cardNumber.matches("\\d{16}")) {
            throw new IllegalArgumentException("core.error.cardNumber.invalid");
        }
        if (cardHolderName == null || cardHolderName.trim().isBlank()) {
            throw new IllegalArgumentException("core.error.cardHolderName.required");
        }
        if (expirationDate == null || !expirationDate.matches("(0[1-9]|1[0-2])/\\d{2}")) {
            throw new IllegalArgumentException("core.error.expirationDate.invalid");
        }
        if (cvv == null || !cvv.matches("\\d{3}")) {
            throw new IllegalArgumentException("core.error.cvv.invalid");
        }
    }
}
