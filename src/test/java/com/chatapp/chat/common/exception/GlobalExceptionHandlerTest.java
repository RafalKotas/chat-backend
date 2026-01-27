package com.chatapp.chat.common.exception;

import com.chatapp.chat.user.exception.EmailAlreadyUsedException;
import com.chatapp.chat.user.exception.UsernameAlreadyUsedException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

class GlobalExceptionHandlerTest {

    GlobalExceptionHandler handler = new GlobalExceptionHandler();

    @Test
    @DisplayName("Should return 400 for EmailAlreadyUsedException")
    void handleEmailAlreadyUsed() {
        // given
        EmailAlreadyUsedException ex = new EmailAlreadyUsedException("test@example.com");

        // when
        ResponseEntity<Object> response = handler.handleEmailAlreadyUsed(ex);

        // then
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);

        Object body = response.getBody();

        assertThat(body).isInstanceOf(Map.class);

        @SuppressWarnings("unchecked")
        Map<String, Object> map = (Map<String, Object>) response.getBody();

        assertThat(map).containsEntry("message", "Email already in use: test@example.com");
    }

    @Test
    @DisplayName("Should return 400 for UsernameAlreadyUsedException")
    void handleUsernameAlreadyUsed() {
        // given
        UsernameAlreadyUsedException ex = new UsernameAlreadyUsedException("johnny");

        // when
        ResponseEntity<Object> response = handler.handleUsernameAlreadyUsed(ex);

        // then
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);

        Object body = response.getBody();

        assertThat(body).isInstanceOf(Map.class);

        @SuppressWarnings("unchecked")
        Map<String, Object> map = (Map<String, Object>) response.getBody();

        assertThat(map).containsEntry("message", "Username already in use: johnny");
    }

    @Test
    @DisplayName("Should return 500 for general Exception")
    void handleGeneralException() {
        // given
        Exception ex = new Exception("boom");

        // when
        ResponseEntity<Object> response = handler.handleGeneral(ex);

        // then
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.INTERNAL_SERVER_ERROR);

        Object body = response.getBody();

        assertThat(body).isInstanceOf(Map.class);

        @SuppressWarnings("unchecked")
        Map<String, Object> map = (Map<String, Object>) response.getBody();

        assertThat(map).containsEntry("message", "Unexpected error occurred");
    }
}