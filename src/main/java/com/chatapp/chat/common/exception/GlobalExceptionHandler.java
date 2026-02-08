package com.chatapp.chat.common.exception;

import com.chatapp.chat.chat.exception.AlreadyInChatException;
import com.chatapp.chat.chat.exception.ChatNotFoundException;
import com.chatapp.chat.chat.exception.UnauthorizedChatOperationException;
import com.chatapp.chat.chat.exception.UserNotInChatException;
import com.chatapp.chat.user.exception.EmailAlreadyUsedException;
import com.chatapp.chat.user.exception.UsernameAlreadyUsedException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.time.Instant;
import java.util.Map;

@ControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler({
            EmailAlreadyUsedException.class,
            UsernameAlreadyUsedException.class,
            AlreadyInChatException.class,
            UnauthorizedChatOperationException.class,
            ChatNotFoundException.class,
            UserNotInChatException.class
    })
    public ResponseEntity<Object> handleDomainExceptions(RuntimeException ex) {

        HttpStatus status = switch (ex) {
            case EmailAlreadyUsedException e -> HttpStatus.BAD_REQUEST;
            case UsernameAlreadyUsedException e -> HttpStatus.BAD_REQUEST;
            case AlreadyInChatException e -> HttpStatus.CONFLICT;
            case UnauthorizedChatOperationException e -> HttpStatus.FORBIDDEN;
            case ChatNotFoundException e -> HttpStatus.NOT_FOUND;
            case UserNotInChatException e -> HttpStatus.NOT_FOUND;
            default -> HttpStatus.INTERNAL_SERVER_ERROR;
        };

        return buildResponse(status, ex.getMessage());
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<Object> handleGeneral(Exception ex) {
        return buildResponse(HttpStatus.INTERNAL_SERVER_ERROR, "Unexpected error occurred");
    }

    private ResponseEntity<Object> buildResponse(HttpStatus status, String message) {
        return new ResponseEntity<>(
                Map.of(
                        "timestamp", Instant.now().toString(),
                        "status", status.value(),
                        "error", status.getReasonPhrase(),
                        "message", message
                ),
                status
        );
    }
}

