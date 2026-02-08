package com.chatapp.chat.chat.exception;

public class UnauthorizedChatOperationException extends RuntimeException {
    public UnauthorizedChatOperationException(String msg) {
        super(msg);
    }
}

