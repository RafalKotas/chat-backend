package com.chatapp.chat.user.exception;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class EmailAlreadyUsedExceptionTest {

    @Test
    @DisplayName("Should build correct exception message")
    void shouldCreateExceptionWithCorrectMessage() {
        // given
        String email = "test@example.com";

        // when
        EmailAlreadyUsedException ex = new EmailAlreadyUsedException(email);

        // then
        assertThat(ex)
                .isInstanceOf(EmailAlreadyUsedException.class)
                .hasMessage("Email already in use: " + email);
    }
}