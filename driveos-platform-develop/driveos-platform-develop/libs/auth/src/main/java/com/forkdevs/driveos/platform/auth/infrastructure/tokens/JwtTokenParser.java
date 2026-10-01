package com.forkdevs.driveos.platform.auth.infrastructure.tokens;

import com.forkdevs.driveos.platform.auth.domain.model.AuthenticatedUser;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.util.List;
import java.util.UUID;

@Component
public class JwtTokenParser {

    @Value("${authorization.jwt.secret:dGhpc2lzYXNlY3JldGtleWZvcmRyaXZlb3NwbGF0Zm9ybXNhYXNhcHBsaWNhdGlvbmluamF2YTI2}")
    private String secret;

    public Claims extractAllClaims(String token) {
        return Jwts.parser()
                .verifyWith(getSignInKey())
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }

    public boolean validateToken(String token) {
        try {
            extractAllClaims(token);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    @SuppressWarnings("unchecked")
    public AuthenticatedUser getAuthenticatedUser(String token) {
        Claims claims = extractAllClaims(token);
        String subject = claims.getSubject();
        
        UUID userId = null;
        if (claims.get("userId") != null) {
            userId = UUID.fromString(claims.get("userId", String.class));
        }

        List<String> roles = (List<String>) claims.get("roles");
        Long branchId = claims.get("branchId", Long.class);

        return AuthenticatedUser.builder()
                .id(userId)
                .username(subject)
                .email(subject)
                .roles(roles != null ? roles : List.of())
                .branchId(branchId)
                .build();
    }

    private SecretKey getSignInKey() {
        byte[] keyBytes = Decoders.BASE64.decode(secret);
        return Keys.hmacShaKeyFor(keyBytes);
    }
}
