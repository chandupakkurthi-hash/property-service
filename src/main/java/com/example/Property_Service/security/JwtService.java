package com.example.Property_Service.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.util.Date;

@Service
public class JwtService {

    @Value("${jwt.secret:}")
    private String secretKey;

    public Claims extractAllClaims(String token) {
        return Jwts.parser()
                .verifyWith(getSignKey())
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }

    public String extractEmail(String token) {
        return extractAllClaims(token).getSubject();
    }

    public Long extractUserId(String token) {
        Object v = extractAllClaims(token).get("userId");
        if (v instanceof Number n) return n.longValue();
        if (v == null) return null;
        return Long.valueOf(v.toString());
    }

    public String extractRole(String token) {
        Object v = extractAllClaims(token).get("role");
        return v != null ? v.toString() : null;
    }

    public boolean isTokenValid(String token) {
        try {
            return !extractAllClaims(token).getExpiration().before(new Date());
        } catch (Exception e) {
            return false;
        }
    }

    private SecretKey getSignKey() {
        if (secretKey == null || secretKey.isBlank()) {
            throw new IllegalStateException("Missing jwt.secret for Property-Service");
        }
        byte[] keyBytes = Decoders.BASE64.decode(secretKey);
        return Keys.hmacShaKeyFor(keyBytes);
    }
}

