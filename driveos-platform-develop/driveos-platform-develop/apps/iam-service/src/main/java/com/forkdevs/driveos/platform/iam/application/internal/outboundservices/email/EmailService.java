package com.forkdevs.driveos.platform.iam.application.internal.outboundservices.email;

/**
 * Port for sending emails (Application Layer).
 * This defines what the application needs without coupling to the infrastructure.
 */
public interface EmailService {

    /**
     * Sends a password recovery email to the user.
     *
     * @param to    the recipient's email address
     * @param token the password recovery token
     */
    void sendPasswordRecoveryEmail(String to, String token);
}
