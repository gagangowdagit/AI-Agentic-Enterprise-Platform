package com.rag.ragbackend.auth.service;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import javax.crypto.SecretKey;
import java.time.Duration;
import java.time.Instant;
import java.util.Date;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class JwtServiceTest {

    private static final String TEST_SECRET = "dGFzazItdGVzdC1vbmx5LXNpZ25pbmcta2V5LTMyLWJ5dGVz";

    private JwtService jwtService;
    private SecretKey signingKey;

    @BeforeEach
    void setUp() {
        jwtService = new JwtService(TEST_SECRET, Duration.ofMinutes(15));
        signingKey = Keys.hmacShaKeyFor(Decoders.BASE64.decode(TEST_SECRET));
    }

    @Test
    void generatedTokenContainsEmployeeIdAndExpiration() {
        JwtService.GeneratedToken generated = jwtService.generateAccessToken(42);

        assertTrue(jwtService.isTokenValid(generated.token()));
        assertEquals(42, jwtService.extractEmployeeId(generated.token()));
        assertEquals(900, generated.expiresInSeconds());

        Claims claims = Jwts.parser()
                .verifyWith(signingKey)
                .build()
                .parseSignedClaims(generated.token())
                .getPayload();
        assertNotNull(claims.getIssuedAt());
        assertNotNull(claims.getExpiration());
        assertEquals(generated.expiresAt().getEpochSecond(), claims.getExpiration().toInstant().getEpochSecond());
    }

    @Test
    void expiredTokenIsRejected() {
        Instant now = Instant.now();
        String expiredToken = Jwts.builder()
                .subject("42")
                .issuedAt(Date.from(now.minusSeconds(120)))
                .expiration(Date.from(now.minusSeconds(60)))
                .signWith(signingKey, Jwts.SIG.HS256)
                .compact();

        assertFalse(jwtService.isTokenValid(expiredToken));
        assertThrows(RuntimeException.class, () -> jwtService.extractEmployeeId(expiredToken));
    }

    @Test
    void tokenSignedWithAnotherKeyIsRejected() {
        SecretKey otherKey = Keys.hmacShaKeyFor(
                "another-test-only-signing-key-at-least-32-bytes".getBytes(java.nio.charset.StandardCharsets.UTF_8));
        String token = Jwts.builder()
                .subject("42")
                .issuedAt(new Date())
                .expiration(Date.from(Instant.now().plusSeconds(900)))
                .signWith(otherKey, Jwts.SIG.HS256)
                .compact();

        assertFalse(jwtService.isTokenValid(token));
    }

    @Test
    void rejectsWeakSigningSecret() {
        String weakSecret = java.util.Base64.getEncoder().encodeToString("short".getBytes(java.nio.charset.StandardCharsets.UTF_8));

        assertThrows(IllegalStateException.class, () -> new JwtService(weakSecret, Duration.ofMinutes(15)));
    }
}