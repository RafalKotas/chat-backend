package com.chatapp.chat.user.exception;

public class EmailAlreadyUsedException extends RuntimeException {
  public EmailAlreadyUsedException(String message) {
    super(message);
  }
}
