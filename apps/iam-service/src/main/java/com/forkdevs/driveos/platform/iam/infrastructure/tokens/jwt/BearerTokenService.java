package com.forkdevs.driveos.platform.iam.infrastructure.tokens.jwt;

import com.forkdevs.driveos.platform.iam.application.internal.outboundservices.tokens.TokenService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.security.core.Authentication;

public interface BearerTokenService extends TokenService {
    String getBearerTokenFrom(HttpServletRequest request);
    @SuppressWarnings("unused")
    String generateToken(Authentication authentication);
}
