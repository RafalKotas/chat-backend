package com.chatapp.chat.security.exception;

public class InvalidJwtSecretException extends RuntimeException {
    public InvalidJwtSecretException(String message) {
        super(message);
    }
}
