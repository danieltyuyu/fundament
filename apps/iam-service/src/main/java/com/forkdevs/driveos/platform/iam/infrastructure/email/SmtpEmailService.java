package com.forkdevs.driveos.platform.iam.infrastructure.email;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import com.forkdevs.driveos.platform.iam.application.internal.outboundservices.email.EmailService;
import org.springframework.stereotype.Service;

import java.util.Locale;

/**
 * Adapter for sending emails using SMTP (Infrastructure Layer).
 */
@Service
public class SmtpEmailService implements EmailService {

    private final JavaMailSender emailSender;
    private final MessageSource messageSource;

    @Value("${app.frontend.url}")
    private String frontendUrl;

    public SmtpEmailService(JavaMailSender emailSender, MessageSource messageSource) {
        this.emailSender = emailSender;
        this.messageSource = messageSource;
    }

    /**
     * Sends a password recovery email to the user.
     *
     * @param to    the recipient's email address
     * @param token the password recovery token
     */
    public void sendPasswordRecoveryEmail(String to, String token) {
        Locale locale = LocaleContextHolder.getLocale();
        String subject = messageSource.getMessage("email.recovery.subject", null, "Recuperación de Contraseña - Atelier", locale);
        String bodyText = messageSource.getMessage("email.recovery.body", new Object[]{frontendUrl, token},
                "Hola,\n\nHemos recibido una solicitud para cambiar tu contraseña.\n" +
                "Haz clic en el siguiente enlace para crear una nueva:\n\n" +
                frontendUrl + "/reset-password?token=" + token + "\n\n" +
                "Si no solicitaste este cambio, ignora este correo.", locale);

        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(to);
        message.setSubject(subject);
        message.setText(bodyText);
        
        emailSender.send(message);
    }
}
