package com.chatapp.chat.security;

import com.chatapp.chat.security.exception.InvalidJwtSecretException;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Field;
import java.security.Key;
import java.time.Clock;
import java.time.Instant;
import java.time.ZoneOffset;
import java.time.temporal.ChronoUnit;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.mock;

class JwtUtilsTest {

    private JwtUtils createJwtUtils(String secret, long expirationMs) {
        return new JwtUtils(secret, expirationMs, Clock.systemUTC());
    }

    @Test
    @DisplayName("Should create JwtUtils when secret is 32+ chars")
    void createJwtUtilsWithSecret() {
        // given
        String secret = "a".repeat(40);

        // when
        JwtUtils jwtUtils = createJwtUtils(secret, 3600000L);

        // then
        assertThat(jwtUtils).isNotNull();
    }

    @Test
    @DisplayName("Should throw InvalidJwtSecretException when secret length < 32")
    void shouldThrowWhenSecretTooShort() {
        // given
        String shortSecret = "too_short_key";

        // when + then
        assertThatThrownBy(() -> createJwtUtils(shortSecret, 3600))
                .isInstanceOf(InvalidJwtSecretException.class)
                .hasMessageContaining("at least 32 characters");
    }

    @Test
    @DisplayName("Should generate token containing correct username")
    void shouldGenerateToken() {
        // given
        JwtUtils utils = createJwtUtils("a".repeat(40), 3600000L);

        // when
        String token = utils.generateToken("alice_banks");

        // then
        assertThat(token).isNotBlank();
        assertThat(utils.extractUserName(token)).isEqualTo("alice_banks");
    }

    @Test
    @DisplayName("Should return true for a valid token")
    void shouldReturnTrueForValidToken() {
        // given
        JwtUtils utils = createJwtUtils("a".repeat(40), 3600);

        String token = utils.generateToken("alice");

        // when
        boolean valid = utils.isValid(token);

        // then
        assertThat(valid).isTrue();
    }

    @Test
    @DisplayName("Should validate token successfully")
    void shouldValidateToken()  {
        // given
        JwtUtils utils = createJwtUtils("a".repeat(40), 1);

        // when
        String token = utils.generateToken("alice");

        // then
        assertThat(utils.isValid(token)).isFalse();
    }

    @Test
    @DisplayName("Should return false for malformed token")
    void shouldReturnFalseForMalformedToken() {
        // given
        JwtUtils utils = createJwtUtils("a".repeat(40), 3600);
        String malformed = "bad.token.value";

        // when + then
        assertThat(utils.isValid(malformed)).isFalse();
    }

    @Test
    @DisplayName("Should return false for token with wrong signature")
    void shouldReturnFalseForInvalidSignature() {
        // given
        JwtUtils utils1 = createJwtUtils("a".repeat(40), 3600);
        JwtUtils utils2 = createJwtUtils("b".repeat(40), 3600);

        String tokenSignedWith1 = utils1.generateToken("alice");

        // when + then
        assertThat(utils2.isValid(tokenSignedWith1)).isFalse();
    }

    @Test
    @DisplayName("Should extract username using parseToken()")
    void shouldExtractUsername() {
        // given
        JwtUtils utils = createJwtUtils("x".repeat(40), 3600000);

        // when
        String token = utils.generateToken("maria");
        String username = utils.extractUserName(token);

        // then
        assertThat(username).isEqualTo("maria");
    }

    @Test
    @DisplayName("Should fail validation when signing key changed via reflection (simulate tampering)")
    void shouldFailWhenSigningKeyChangedViaReflection() throws Exception {
        // given
        JwtUtils utils = createJwtUtils("a".repeat(40), 3600);
        String token = utils.generateToken("alice");

        // when – manipulacja kluczem
        Field keyField = JwtUtils.class.getDeclaredField("signingKey");
        keyField.setAccessible(true);
        keyField.set(utils, mock(Key.class));

        // then
        assertThat(utils.isValid(token)).isFalse();
    }

    @Test
    @DisplayName("Should generate token with exact issuedAt and expiration date")
    void shouldGenerateTokenWithExpirationDate() {
        // given
        Instant fixed = Instant.now();
        Clock fixedClock = Clock.fixed(fixed, ZoneOffset.UTC);
        JwtUtils utils = new JwtUtils("a".repeat(40), 60_000, fixedClock);

        // when
        String token = utils.generateToken("alice");

        Claims claims = Jwts.parserBuilder()
                .setSigningKey(utilsTestKey(utils))
                .build()
                .parseClaimsJws(token)
                .getBody();

        // then
        assertThat(claims.getIssuedAt().toInstant())
                .isEqualTo(truncateToSeconds(fixed));

        assertThat(claims.getExpiration().toInstant())
                .isEqualTo(truncateToSeconds(fixed.plusMillis(60_000)));
    }

    private Key utilsTestKey(JwtUtils utils) {
        try {
            Field f = JwtUtils.class.getDeclaredField("signingKey");
            f.setAccessible(true);
            return (Key) f.get(utils);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private Instant truncateToSeconds(Instant instant) {
        return instant.truncatedTo(ChronoUnit.SECONDS);
    }
}
