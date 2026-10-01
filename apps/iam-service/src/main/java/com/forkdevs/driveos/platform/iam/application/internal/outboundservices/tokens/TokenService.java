package com.forkdevs.driveos.platform.iam.application.internal.outboundservices.tokens;

public interface TokenService {
    String generateToken(String username);
    String generateToken(String username, java.util.Map<String, Object> claims);
    String getUsernameFromToken(String token);
    boolean validateToken(String token);
}
