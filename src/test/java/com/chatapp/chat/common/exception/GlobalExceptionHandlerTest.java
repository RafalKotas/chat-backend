package com.chatapp.chat.common.exception;

import com.chatapp.chat.chat.exception.AlreadyInChatException;
import com.chatapp.chat.chat.exception.ChatNotFoundException;
import com.chatapp.chat.chat.exception.UnauthorizedChatOperationException;
import com.chatapp.chat.chat.exception.UserNotInChatException;
import com.chatapp.chat.user.exception.EmailAlreadyUsedException;
import com.chatapp.chat.user.exception.UsernameAlreadyUsedException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

class GlobalExceptionHandlerTest {

    GlobalExceptionHandler subject = new GlobalExceptionHandler();

    @Test
    @DisplayName("Should return 400 for EmailAlreadyUsedException")
    void handleEmailAlreadyUsed() {
        // given
        EmailAlreadyUsedException ex = new EmailAlreadyUsedException("test@example.com");

        // when
        ResponseEntity<Object> response = subject.handleDomainExceptions(ex);

        // then
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        assertMessage(response, "Email already in use: test@example.com");
    }

    @Test
    @DisplayName("Should return 400 for UsernameAlreadyUsedException")
    void handleUsernameAlreadyUsed() {
        // given
        UsernameAlreadyUsedException ex = new UsernameAlreadyUsedException("johnny");

        // when
        ResponseEntity<Object> response = subject.handleDomainExceptions(ex);

        // then
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        assertMessage(response, "Username already in use: johnny");
    }

    @Test
    @DisplayName("Should return 409 for AlreadyInChatException")
    void handleAlreadyInChat() {
        // given
        AlreadyInChatException ex = new AlreadyInChatException("User already in chat");

        // when
        ResponseEntity<Object> response = subject.handleDomainExceptions(ex);

        // then
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CONFLICT);
        assertMessage(response, "User already in chat");
    }

    @Test
    @DisplayName("Should return 403 for UnauthorizedChatOperationException")
    void handleUnauthorizedChatOperation() {
        // given
        UnauthorizedChatOperationException ex =
                new UnauthorizedChatOperationException("Cannot modify direct chat");

        // when
        ResponseEntity<Object> response = subject.handleDomainExceptions(ex);

        // then
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.FORBIDDEN);
        assertMessage(response, "Cannot modify direct chat");
    }

    @Test
    @DisplayName("Should return 404 for ChatNotFoundException")
    void handleChatNotFound() {
        // given
        ChatNotFoundException ex = new ChatNotFoundException("Chat not found");

        // when
        ResponseEntity<Object> response = subject.handleDomainExceptions(ex);

        // then
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
        assertMessage(response, "Chat not found");
    }

    @Test
    @DisplayName("Should return 404 for UserNotInChatException")
    void handleUserNotInChat() {
        // given
        UserNotInChatException ex = new UserNotInChatException("User not in chat");

        // when
        ResponseEntity<Object> response = subject.handleDomainExceptions(ex);

        // then
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
        assertMessage(response, "User not in chat");
    }

    @Test
    @DisplayName("Should return 500 for general Exception")
    void handleGeneralException() {
        // given
        Exception ex = new Exception("boom");

        // when
        ResponseEntity<Object> response = subject.handleGeneral(ex);

        // then
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.INTERNAL_SERVER_ERROR);
        assertMessage(response, "Unexpected error occurred");
    }

    private void assertMessage(ResponseEntity<Object> response, String expectedMessage) {
        assertThat(response.getBody()).isInstanceOf(Map.class);

        @SuppressWarnings("unchecked")
        Map<String, Object> map = (Map<String, Object>) response.getBody();

        assertThat(map).containsEntry("message", expectedMessage);
    }
}
