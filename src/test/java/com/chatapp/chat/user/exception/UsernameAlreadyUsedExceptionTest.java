package com.chatapp.chat.user.exception;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class UsernameAlreadyUsedExceptionTest {

    @Test
    @DisplayName("Should build correct exception message")
    void shouldCreateExceptionWithCorrectMessage() {
        // given
        String username = "raf";

        // when
        UsernameAlreadyUsedException ex = new UsernameAlreadyUsedException(username);

        // then
        assertThat(ex)
                .isInstanceOf(UsernameAlreadyUsedException.class)
                .hasMessage("Username already in use: " + username);
    }
}