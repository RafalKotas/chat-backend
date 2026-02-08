package com.chatapp.chat.chat.exception;

public class AlreadyInChatException extends RuntimeException {
    public AlreadyInChatException(String msg) {
        super(msg);
    }
}
