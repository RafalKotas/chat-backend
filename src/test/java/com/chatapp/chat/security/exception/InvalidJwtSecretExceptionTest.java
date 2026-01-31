package com.chatapp.chat.security.exception;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class InvalidJwtSecretExceptionTest {

    @Test
    @DisplayName("Should store the provided message")
    void shouldStoreProvidedMessage() {
        // given
        String message = "JWT secret too short";

        // when
        InvalidJwtSecretException exception = new InvalidJwtSecretException(message);

        // then
        assertThat(exception.getMessage()).isEqualTo(message);
    }

    @Test
    @DisplayName("Should be instance of RuntimeException")
    void shouldExtendRuntimeException() {
        // given
        InvalidJwtSecretException exception = new InvalidJwtSecretException("msg");

        // then
        assertThat(exception).isInstanceOf(RuntimeException.class);
    }

    @Test
    @DisplayName("Should accept null message")
    void shouldAcceptNullMessage() {
        // given / when
        InvalidJwtSecretException exception = new InvalidJwtSecretException(null);

        // then
        assertThat(exception.getMessage()).isNull();
    }
}