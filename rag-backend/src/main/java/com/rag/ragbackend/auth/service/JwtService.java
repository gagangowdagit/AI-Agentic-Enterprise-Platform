package com.rag.ragbackend.auth.service;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.time.Duration;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Date;

@Service
public class JwtService {

    private static final int MINIMUM_SECRET_BYTES = 32;

    private final SecretKey signingKey;
    private final Duration accessTokenLifetime;

    public JwtService(@Value("${app.jwt.secret}") String encodedSecret,
                      @Value("${app.jwt.access-token-lifetime:PT15M}") Duration accessTokenLifetime) {
        if (encodedSecret == null || encodedSecret.isBlank()) {
            throw new IllegalStateException("JWT_SECRET must contain a Base64-encoded signing key");
        }

        byte[] secretBytes;
        try {
            secretBytes = Decoders.BASE64.decode(encodedSecret);
        } catch (RuntimeException exception) {
            throw new IllegalStateException("JWT_SECRET must be valid Base64", exception);
        }
        if (secretBytes.length < MINIMUM_SECRET_BYTES) {
            throw new IllegalStateException("JWT_SECRET must decode to at least 32 bytes for HS256");
        }
        if (accessTokenLifetime == null || accessTokenLifetime.getSeconds() <= 0) {
            throw new IllegalStateException("JWT access-token lifetime must be at least one second");
        }

        this.signingKey = Keys.hmacShaKeyFor(secretBytes);
        this.accessTokenLifetime = accessTokenLifetime;
    }

    public GeneratedToken generateAccessToken(Integer employeeId) {
        if (employeeId == null) {
            throw new IllegalArgumentException("Employee identifier is required to generate a JWT");
        }

        Instant issuedAt = Instant.now().truncatedTo(ChronoUnit.SECONDS);
        Instant expiresAt = issuedAt.plusSeconds(accessTokenLifetime.toSeconds());
        String token = Jwts.builder()
                .subject(employeeId.toString())
                .issuedAt(Date.from(issuedAt))
                .expiration(Date.from(expiresAt))
                .signWith(signingKey, Jwts.SIG.HS256)
                .compact();

        return new GeneratedToken(token, expiresAt, accessTokenLifetime.toSeconds());
    }

    public boolean isTokenValid(String token) {
        try {
            parseClaims(token);
            return true;
        } catch (JwtException | IllegalArgumentException exception) {
            return false;
        }
    }

    public Integer extractEmployeeId(String token) {
        String subject = parseClaims(token).getSubject();
        try {
            return Integer.valueOf(subject);
        } catch (NumberFormatException exception) {
            throw new JwtException("JWT subject is not a valid employee identifier", exception);
        }
    }

    private Claims parseClaims(String token) {
        if (token == null || token.isBlank()) {
            throw new IllegalArgumentException("JWT is required");
        }

        return Jwts.parser()
                .verifyWith(signingKey)
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }

    public record GeneratedToken(String token, Instant expiresAt, long expiresInSeconds) {
    }
}