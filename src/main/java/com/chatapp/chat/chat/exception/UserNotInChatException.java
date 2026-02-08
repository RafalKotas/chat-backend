package com.chatapp.chat.chat.exception;

public class UserNotInChatException extends RuntimeException {
    public UserNotInChatException(String msg) {
        super(msg);
    }
}
