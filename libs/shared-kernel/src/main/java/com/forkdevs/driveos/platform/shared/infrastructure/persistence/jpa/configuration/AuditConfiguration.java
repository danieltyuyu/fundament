package com.forkdevs.driveos.platform.shared.infrastructure.persistence.jpa.configuration;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.domain.AuditorAware;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.Optional;
import java.util.UUID;

/**
 * Configuration class for setting up JPA auditing in the application.
 * This class defines a bean for providing the current auditor (user) information.
 * The auditor information is typically used to automatically populate auditing fields such as createdBy and lastModifiedBy in JPA entities.
 * In this example, the auditor is represented as a UUID, and a default value is returned for demonstration purposes. In a real application, you would replace this with logic to retrieve the actual user information from the security context or authentication mechanism in use (e.g., Spring Security).
 * @author Joel Huamani Estefanero
 */
@Configuration
@EnableJpaAuditing
public class AuditConfiguration {

    /**
     * Defines a bean that provides the current auditor information for JPA auditing. The auditor is represented as a UUID, and a default value is returned for demonstration purposes. In a real application, you would replace this with logic to retrieve the actual user information from the security context or authentication mechanism in use (e.g., Spring Security).
     * @return An instance of AuditorAware that provides the current auditor information as a UUID.
     */
    @Bean
    public AuditorAware<UUID> auditorProvider() {
        return () -> {
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

            if (authentication == null || !authentication.isAuthenticated() || "anonymousUser".equals(authentication.getPrincipal())) {
                return Optional.empty();
            }

            Object principal = authentication.getPrincipal();
            if (principal == null) {
                return Optional.empty();
            }

            try {
                var method = principal.getClass().getMethod("getId");
                Object idObj = method.invoke(principal);
                if (idObj instanceof UUID uuid) {
                    return Optional.of(uuid);
                } else if (idObj instanceof String strId) {
                    return Optional.of(UUID.fromString(strId));
                }
            } catch (Exception ignored) {
            }

            return Optional.empty();
        };
    }
}