package com.chatapp.chat.security;

import com.chatapp.chat.security.exception.InvalidJwtSecretException;
import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.security.Key;
import java.time.Clock;
import java.time.Instant;
import java.util.Date;

@Component
public class JwtUtils {

    private final Key signingKey;
    private final long expirationMs;
    private final Clock clock;

    /**
     * Production constructor — used by Spring Boot.
     */
    @Autowired
    public JwtUtils(
            @Value("${app.jwt.secret}") String secret,
            @Value("${app.jwt.expiration-ms}") long expirationMs
    ) {
        this(secret, expirationMs, Clock.systemUTC());
    }

    /**
     * Test constructor — allows injecting fixed Clock.
     */
    public JwtUtils(String secret, long expirationMs, Clock clock) {
        if (secret.length() < 32) {
            throw new InvalidJwtSecretException(
                    "JWT secret key must be at least 32 characters long."
            );
        }

        this.signingKey = Keys.hmacShaKeyFor(secret.getBytes());
        this.expirationMs = expirationMs;
        this.clock = clock;
    }

    public String generateToken(String subject) {
        Instant now = clock.instant();
        Instant expiry = now.plusMillis(expirationMs);

        return Jwts.builder()
                .setSubject(subject)
                .setIssuedAt(Date.from(now))
                .setExpiration(Date.from(expiry))
                .signWith(signingKey, SignatureAlgorithm.HS256)
                .compact();
    }

    public String extractUserName(String token) {
        return parseToken(token).getBody().getSubject();
    }

    public boolean isValid(String token) {
        try {
            parseToken(token);
            return true;
        } catch (JwtException e) {
            return false;
        }
    }

    private Jws<Claims> parseToken(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(signingKey)
                .build()
                .parseClaimsJws(token);
    }
}
